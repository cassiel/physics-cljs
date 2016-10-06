(ns physics-cljs.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as mw]
            [physics-cljs.protocols :as px]
            [physics-cljs.matter-core :as m]
            [physics-cljs.player :as player]
            [cljsjs.jquery]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

;; Verbatim from the Quil/ClojureScript example:

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 60)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:color 0
   :angle 0})

(defn update-state [state]
  ;; Tickle the physics engine:

  ;;(.rotate m/BODY pt/obstacle 0.02)
  (.update m/ENGINE m/the-engine (/ 1000 60))

  ; Update sketch state by changing circle color and position.
  {:color (mod (+ (:color state) 0.7) 255)
   :angle (+ (:angle state) 0.1)})

(defn draw-rect [r]
  (q/with-translation [(-> r .-position .-x)
                       (-> r .-position .-y)]
    (q/with-rotation [(-> r .-angle)]
      (let [[w h] (.-_size r)]
        (q/rect 0 0 w h))))
  )

(def players [(player/box :position [200 200] :size [10 10])])

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  ; Set circle color.
  (q/fill 255)
  ; Calculate x and y coordinates of the circle.

  (q/rect-mode :center)

  (doseq [p players] (px/draw p)))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(m/add-players players)

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
