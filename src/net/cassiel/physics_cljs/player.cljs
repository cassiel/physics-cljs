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

(defn box [& {:keys [position size colour stroke opts]}]
  (let [[x y] position
        [w h] size
        body (-> (.rectangle m/BODIES x y w h (clj->js opts))
                 remember-size)]
    (reify px/PLAYER
      (set-angle [_ angle] (.setAngle m/BODY body angle))
      (draw [_]
        (q/with-translation [(.. body -position -x)
                             (.. body -position -y)]
          (q/with-rotation [(.. body -angle)]
            (let [[w h] (get-property body :size)]
              (if stroke (q/stroke stroke) (q/no-stroke))
              (apply q/fill (or colour [255 255 255]))
              (q/rect 0 0 w h)))))

      (get-body [_] body))))

(defn disc [& {:keys [position radius colour opts]}]
  (let [[x y] position
        body (-> (.circle m/BODIES x y radius (clj->js opts))
                 remember-size)]
    (reify px/PLAYER
      (set-angle [_ angle] (.setAngle m/BODY body angle))
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

  ;; Positioning is tricky; the position is set by matter.js as the centroid
  ;; of the vertices. We want it at [0, 0].

  (let [[x y] position
        vs (clj->js (map (fn [[x y]] {:x x :y y}) vertices))
        vs-centroid (.centre m/VERTICES vs)
        [cx cy] ((juxt #(.-x %) #(.-y %)) vs-centroid)
        body (.fromVertices m/BODIES x y vs (clj->js opts))
        _ (println "Centre of source vertices: " (.centre m/VERTICES vs))
        _ (println "Centre of generated body: " (.centre m/VERTICES (.-vertices body)))
        ]
    (.translate m/BODY body vs-centroid)
    (reify px/PLAYER
      (set-angle [_ angle]
        (let [xp (+ x
                    (* cx (js/Math.cos angle))
                    (- (* cy (js/Math.sin angle))))
              yp (+ y
                    (* cy (js/Math.cos angle))
                    (* cx (js/Math.sin angle)))]
          (.setPosition m/BODY body #js {:x xp :y yp})
          (.setAngle m/BODY body angle)))
      (draw [_]
        (let [xp (.. body -position -x)
              yp (.. body -position -y)]
          (q/with-translation [xp yp]
            ;; Rotation seems to be applied in-place, so no need:
            (q/with-rotation [0 #_ (.. body -angle)]
              (q/no-stroke)
              (q/fill 255 0 0)
              (q/ellipse 0 0 10 10)
              (q/fill 0)
              (doseq [v (.-vertices body)]
                (q/ellipse (- (.-x v) xp)
                           (- (.-y v) yp) 5 5))))))
      (get-body [_] body))))
