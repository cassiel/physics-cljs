(ns net.cassiel.physics-cljs.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as mw]
            [net.cassiel.physics-cljs.protocols :as px]
            [net.cassiel.physics-cljs.matter-core :as m]
            [net.cassiel.physics-cljs.player :as player]
            [cljsjs.jquery]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

;; Verbatim from the Quil/ClojureScript example:

(defn setup []
  ;; Set frame rate to 60 frames per second.
  (q/frame-rate 60)
  ;; setup function returns initial state.
  (let [engine (m/new-engine)
        statics (for [x (range 100 1200 75)
                      y (range 100 700 75)]
                  (player/box :position [x y]
                              :size [50 10]
                              :opts {:isStatic true}))
        players (for [x (range 125 1200 50)]
                  (player/disc :position [x 50]
                               :radius 10
                               :colour [0 0 0]
                               :opts {:restitution 1.1}))
        all (concat statics players)]
    (m/add-players engine all)
    {:engine engine
     :statics statics
     :players players
     :all all
     :rands (repeatedly rand)}))

(defn update-state [{:keys [engine statics rands] :as state}]
  ;; Tickle the physics engine:

  (doseq [[i p r] (map #(vec [%1 %2 %3]) (range) statics rands)]
    (.rotate m/BODY (px/get-body p) (- 0.15 (* r 0.3))))

  (.update m/ENGINE engine (/ 1000 60))
  state)

(defn draw-state [{:keys [all] :as state}]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  ; Set circle color.
  (q/fill 255)
  ; Calculate x and y coordinates of the circle.

  (q/rect-mode :center)

  (doseq [p all] (px/draw p)))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(q/defsketch main
  :host "canvas"
  :size [(.-innerWidth js/window)
         (.-innerHeight js/window)]
                                        ; setup function called only once, during sketch initialization.
  :setup setup
                                        ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw #'draw-state
                                        ; This sketch uses functional-mode middleware.
                                        ; Check quil wiki for more info about middlewares and particularly
                                        ; fun-mode.
  :middleware [mw/fun-mode])
