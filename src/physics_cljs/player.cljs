(ns physics-cljs.player
  (:require [physics-cljs.matter-core :as m]
            [physics-cljs.protocols :as px]
            [quil.core :as q :include-macros true])
  )

(defn remember-size [body]
  (let [min (-> body .-bounds .-min)
        max (-> body .-bounds .-max)]
    (set! (.-_size body)
          #js [(- (.-x max) (.-x min))
               (- (.-y max) (.-y min))])
    body))

(defn box [& {:keys [position size opts]}]
  (let [[x y] position
        [w h] size
        body (-> (.rectangle m/BODIES x y w h (clj->js opts)) remember-size)]
    (reify px/PLAYER
      (draw [_]
        (q/with-translation [(-> body .-position .-x)
                             (-> body .-position .-y)]
          (q/with-rotation [(-> body .-angle)]
            (let [[w h] (.-_size body)]
              (q/rect 0 0 w h)))))

      (get-body [_] body))))
