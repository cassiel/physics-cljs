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
        statics1 (for [x (range 100 1200 75)
                      y (range 100 700 75)]
                  (player/box :position [x y]
                              :size [50 10]
                              :opts {:isStatic true}))
        statics [(player/polygon :position [200 200]
                                 :vertices [[0 0] [100 0] [100 100] [0 20]]
                                 :opts {:isStatic true})]
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

  #_ (doseq [[i p r] (map #(vec [%1 %2 %3]) (range) statics rands)
          :let [r 0.45]]
       (.rotate m/BODY (px/get-body p) (- 0.15 (* r 0.1))))
  (px/set-angle (first statics) (* (q/frame-count) 0.05))

  (.update m/ENGINE engine (/ 1000 60))
  state)

(defn draw-state [{:keys [all] :as state}]
  (q/background 240)
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
  ;; setup function called only once, during sketch initialization.
  :setup setup
  ;; update-state is called on each iteration before draw-state.
  :update update-state
  :draw #'draw-state
  ;; This sketch uses functional-mode middleware.
  ;; Check quil wiki for more info about middlewares and particularly
  ;; fun-mode.
  :middleware [mw/fun-mode])
