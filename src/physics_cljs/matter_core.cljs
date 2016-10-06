(ns physics-cljs.matter-core
  (:require [physics-cljs.protocols :as px]
            [cljsjs.matter]))

(def ENGINE js/Matter.Engine)
(def BODY js/Matter.Body)
(def BODIES js/Matter.Bodies)
(def WORLD js/Matter.World)

(def the-engine (.create ENGINE))

(defn add-players [players]
  (.add WORLD
        (.-world the-engine)
        (clj->js (map px/get-body players))))
