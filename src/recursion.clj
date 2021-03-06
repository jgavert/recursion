(ns recursion)

(defn product [coll]
  (if (empty? coll) 1 (* (first coll) (product (rest coll)))))

(defn singleton? [coll]
  (and (not (empty? coll)) (empty? (rest coll))))

(defn my-last [[a & rst :as coll]]
  (if (singleton? coll) a
    (if (empty? coll) nil (my-last rst))))

(defn max-element [[a b & rst :as a-seq]]
  (if (singleton? a-seq) a
    (if (empty? a-seq) nil
      (max-element (cons (max a b) rst )))))

(defn seq-max [seq-1 seq-2]
  (let [fun (fn ! [x y] (if (empty? x) false
                        (if (empty? y) true (! (rest x) (rest y)))))]
    (if (fun seq-1 seq-2) seq-1 seq-2)))

(defn longest-sequence [[a b & rst :as a-seq]]
  (if (singleton? a-seq) a
    (if (empty? a-seq) nil
      (longest-sequence (cons (seq-max a b) rst )))))

(defn my-filter [pred? [a & rst :as a-seq]]
  (if (singleton? a-seq)
    (if (pred? a) [a] [])
    (if (pred? a) (cons a (my-filter pred? rst)) (my-filter pred? rst))))

(defn sequence-contains? [elem [a & rst :as a-seq]]
  (if (empty? a-seq) false
    (if (== elem a) true (sequence-contains? elem rst))))

(defn my-take-while [pred? [a & rst :as a-seq]]
  (if (empty? a-seq) []
    (if (pred? a) (cons a (my-take-while pred? rst)) [])))

(defn my-drop-while [pred? [a & rst :as a-seq]]
    (if (empty? a-seq) []
      (if (pred? a) (my-drop-while pred? rst) a-seq)))

(defn seq= [[a & rst-a :as a-seq] [b & rst-b :as b-seq]]
  (let [ae? (empty? a-seq)
        be? (empty? b-seq)]
    (if (and ae? be?) true
      (if (or ae? be?) false
        (if (== a b) (seq= rst-a rst-b) false)))))

(defn my-map [f [a & rst-a :as a-seq] [b & rst-b :as b-seq]]
  (let [ae? (empty? a-seq)
        be? (empty? b-seq)]
    (if (or ae? be?) []
      (cons (f a b) (my-map f rst-a rst-b)))))

(defn power [n k]
  (if (zero? k) 1
    (if (== k 1) n
      (* n (power n (- k 1))))))

(defn fib [n]
  (if (== 0 n) 0
    (if (== 1 n) 1
      (+ (fib (- n 1)) (fib (- n 2))))))

(defn my-repeat [how-many-times what-to-repeat]
  (if (<= how-many-times 0) []
    (cons what-to-repeat (my-repeat (- how-many-times 1) what-to-repeat))))

(defn my-range [up-to]
  (if (<= up-to 0) []
    (cons (- up-to 1) (my-range (- up-to 1)))))

(defn tails [a-seq]
  (if (empty? a-seq) '(())
    (cons a-seq (tails (rest a-seq)))))

(defn inits [a-seq]
  (reverse (map reverse (tails (reverse a-seq)))))

(defn rotations [a-seq]
  (if (empty? a-seq) '(())
    (let [f (fn a [x y] (if (zero? y) []
                          (cons x (a (#(concat (rest %) [(first %)]) x) (- y 1)))))]
      (f a-seq (count a-seq)))))

(defn my-frequencies-helper [freqs a-seq]
  (if (empty? a-seq) {}
    (let [a (first a-seq)]
      (merge (my-frequencies-helper {} (my-filter #(not (= a %)) a-seq))
             {a (count (my-filter #(= a %) a-seq))}))))

(defn my-frequencies [a-seq]
  (my-frequencies-helper {} a-seq))

(defn un-frequencies [a-map]
  (if (empty? a-map) []
    (let [a (first a-map)
          rst (rest a-map)]
      (concat (repeat (val a) (key a)) (un-frequencies rst)))))

(defn my-take [n coll]
  (if (or (empty? coll) (zero? n)) [] (cons (first coll) (my-take (- n 1) (rest coll)))))

(defn my-drop [n coll]
  (if (or (empty? coll) (zero? n)) coll (my-drop (- n 1) (rest coll))))

(defn halve [a-seq]
  (let [half (int (/ (count a-seq) 2))]
    [(my-take half a-seq) (my-drop half a-seq)]))

(defn seq-merge
  ([[a & rsta :as a-seq] [b & rstb :as b-seq]]
    (if (and (empty? a-seq) (empty? b-seq)) []
      (if (empty? a-seq)
        (cons b (seq-merge [] rstb))
        (if (empty? b-seq)
          (cons a (seq-merge [] rsta))
          (if (<= a b)
            (cons a (seq-merge rsta b-seq))
            (cons b (seq-merge rstb a-seq))))))))

(defn merge-sort [[a & rst :as a-seq]]
  (if (empty? a-seq) []
    (if (empty? rst) [a]
      (let [[a-s b-s] (halve a-seq)]
        (seq-merge (merge-sort a-s) (merge-sort b-s))))))

(defn take-longest [pred [a b & rst :as a-seq]]
  (if (empty? a-seq) []
    (if (empty? (rest a-seq)) a
      (if (not(apply pred b)) a (take-longest pred (rest a-seq))))))

(defn split-into-monotonics [a-seq]
  (if (empty? a-seq) []
    (let [got (take-longest <= (rest (inits a-seq)))
          gotb (take-longest >= (rest (inits a-seq)))
          longest (seq-max got gotb)
          drp (drop (count longest) a-seq)]
      (cons longest (split-into-monotonics drp)))))

(defn permutations [a-set]
  (if (<= (count a-set) 2) (rotations a-set)
    (let [sets (rotations a-set)
          all (apply concat (map  #(map (fn [x] (concat [(first %)] x)) (permutations (rest %))) sets))]
      all)))

(defn powerset [a-set]
  (if (empty? a-set) '#{#{}}
    (if (== 1 (count a-set)) (clojure.set/union '#{#{}} #{a-set})
      (let [sets (map set (map #(rest %) (rotations a-set)))]
        (clojure.set/union (apply clojure.set/union (map powerset sets)) sets #{(set a-set)})))))
