(ns physics-cljs.physics-test
  (:require [cljsjs.matter]))

(def E js/Matter.Engine)
(def b js/Matter.Body)
(def B js/Matter.Bodies)
(def W js/Matter.World)

(def engine (.create E))

(defn remember-size [body]
  (let [min (-> body .-bounds .-min)
        max (-> body .-bounds .-max)]
    (set! (.-_size body)
          #js [(- (.-x max) (.-x min))
               (- (.-y max) (.-y min))])
    body))

(def box-a (-> (.rectangle B 400 200 40 80) remember-size))
(def box-b (-> (.rectangle B 445 50 80 80) remember-size))
(def obstacle (-> (.rectangle B 410 300 10 100 #js {:isStatic true}) remember-size))
(def ground (-> (.rectangle B 300 590 600 20 #js {:isStatic true}) remember-size))

(.add W (.-world engine) #js [box-a box-b obstacle ground])
