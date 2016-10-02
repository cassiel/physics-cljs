(ns physics-cljs.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [physics-cljs.physics-test :as pt]))

(enable-console-print!)

(println "This text is printed from src/physics-cljs/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

;; Verbatim from the Quil/ClojureScript example:

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:color 0
   :angle 0})

(defn update-state [state]
  ;; Tickle the physics engine:

  (.update pt/E pt/engine (/ 1000 60))

  ; Update sketch state by changing circle color and position.
  {:color (mod (+ (:color state) 0.7) 255)
   :angle (+ (:angle state) 0.1)})

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  ; Set circle color.
  (q/fill (:color state) 255 255)
  ; Calculate x and y coordinates of the circle.
  (let [angle (:angle state)
        x (* 150 (q/cos angle))
        y (* 150 (q/sin angle))]
    ; Move origin point to the center of the sketch.
    #_ (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
                                        ; Draw the circle.q
      (q/ellipse x y 100 100))
    ;;(println (-> pt/box-a .-position .-x))
    (q/ellipse (-> pt/box-a .-position .-x)
               (-> pt/box-a .-position .-y)
               20
               20)
    (q/ellipse (-> pt/box-b .-position .-x)
               (-> pt/box-b .-position .-y)
               20
               20)
    ))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(q/defsketch my-sketch
  :host "canvas"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
