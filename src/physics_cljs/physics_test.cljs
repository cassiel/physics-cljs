(ns physics-cljs.physics-test
  (:require [cljsjs.matter]))

(def E js/Matter.Engine)
(def B js/Matter.Bodies)
(def W js/Matter.World)

(def engine (.create E))

(def box-a (.rectangle B 400 200 80 80))
(def box-b (.rectangle B 450 50 80 80))
(def ground (.rectangle B 400 610 810 60 #js {:isStatic true}))

(.add W (.-world engine) #js [box-a box-b ground])
