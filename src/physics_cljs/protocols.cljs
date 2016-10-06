(ns physics-cljs.protocols)

(defprotocol PLAYER
  (draw [_] "Draw body at its position and rotation.")
  (get-body [_] "Get the matter.js body of this player."))
