(ns physics-cljs.matter-core
  (:require [physics-cljs.protocols :as px]
            [cljsjs.matter]))

(def ENGINE js/Matter.Engine)
(def BODY js/Matter.Body)
(def BODIES js/Matter.Bodies)
(def WORLD js/Matter.World)

(defn new-engine [] (.create ENGINE #js {:timing #js {:timeScale 0.5}}))

(defn add-players [engine players]
  (.add WORLD
        (.-world engine)
        (clj->js (map px/get-body players))))
