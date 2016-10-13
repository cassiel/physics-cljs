(ns net.cassiel.physics-cljs.player
  (:require [net.cassiel.physics-cljs.matter-core :as m]
            [net.cassiel.physics-cljs.protocols :as px]
            [quil.core :as q :include-macros true])
  )

(defn remember-size [body]
  (let [min (-> body .-bounds .-min)
        max (-> body .-bounds .-max)]
    (set! (.-_size body)
          #js [(- (.-x max) (.-x min))
               (- (.-y max) (.-y min))])
    body))

(defn box [& {:keys [position size colour opts]}]
  (let [[x y] position
        [w h] size
        body (-> (.rectangle m/BODIES x y w h (clj->js opts)) remember-size)]
    (reify px/PLAYER
      (draw [_]
        (q/with-translation [(.. body -position -x)
                             (.. body -position -y)]
          (q/with-rotation [(.. body -angle)]
            (let [[w h] (.. body -_size)]
              (apply q/fill (or colour [255 255 255]))
              (q/rect 0 0 w h)))))

      (get-body [_] body))))

(defn disc [& {:keys [position radius colour opts]}]
  (let [[x y] position
        body (-> (.circle m/BODIES x y radius (clj->js opts)) remember-size)]
    (reify px/PLAYER
      (draw [_]
        (q/with-translation [(.. body -position -x)
                             (.. body -position -y)]
          (let [[w h] (.. body -_size)]
            (apply q/fill (or colour [255 255 255]))
            (q/ellipse 0 0 w h))))

      (get-body [_] body))))
