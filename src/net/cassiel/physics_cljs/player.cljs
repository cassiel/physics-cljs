(ns net.cassiel.physics-cljs.player
  (:require [net.cassiel.physics-cljs.matter-core :as m]
            [net.cassiel.physics-cljs.protocols :as px]
            [quil.core :as q :include-macros true]))

(def PROPS "PROPS")

(defn get-property [js-obj f]
  (f (aget js-obj PROPS)))

(defn swap-property!
  "`f` probably `assoc`, `update` etc."
  [js-obj f]
  (aset js-obj PROPS (get-property js-obj f)))

(defn remember-size [body]
  (let [min (-> body .-bounds .-min)
        max (-> body .-bounds .-max)]
    (swap-property! body #(assoc % :size [(- (.-x max) (.-x min))
                                          (- (.-y max) (.-y min))]))
    body))

(defn box [& {:keys [position size colour opts]}]
  (let [[x y] position
        [w h] size
        body (-> (.rectangle m/BODIES x y w h (clj->js opts))
                 remember-size)]
    (reify px/PLAYER
      (draw [_]
        (q/with-translation [(.. body -position -x)
                             (.. body -position -y)]
          (q/with-rotation [(.. body -angle)]
            (let [[w h] (get-property body :size)]
              (apply q/fill (or colour [255 255 255]))
              (q/rect 0 0 w h)))))

      (get-body [_] body))))

(defn disc [& {:keys [position radius colour opts]}]
  (let [[x y] position
        body (-> (.circle m/BODIES x y radius (clj->js opts))
                 remember-size)]
    (reify px/PLAYER
      (draw [_]
        (q/with-translation [(.. body -position -x)
                             (.. body -position -y)]
          (let [[w h] (get-property body :size)]
            (apply q/fill (or colour [255 255 255]))
            (q/ellipse 0 0 w h))))

      (get-body [_] body))))

(defn polygon
  "Arbitrary polygon."
  [& {:keys [position vertices opts]}]

  (let [[x y] position
        vs (clj->js (map (fn [[x y]] {:x x :y y}) vertices))
        vs1 (.fromPath m/VERTICES "400 150 400 250 300 300")
        body (.fromVertices m/BODIES x y vs (clj->js opts))]
    (reify px/PLAYER
      (draw [_]
        (let [x0 (.. body -position -x)
              y0 (.. body -position -y)]
          (q/with-translation [x0 y0]
            (q/with-rotation [(.. body -angle)]
              (q/no-stroke)
              (q/fill 0)
              (doseq [v (.-vertices body)]
                (q/ellipse (- (.-x v) x0)
                           (- (.-y v) y0) 5 5))
              (q/fill 255 0 0)
              (q/ellipse 0 0 5 5)))))
      (get-body [_] body))))
