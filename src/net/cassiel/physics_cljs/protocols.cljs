(ns net.cassiel.physics-cljs.protocols)

(defprotocol PLAYER
  (set-angle [_ angle] "Set body angle (allowing for any offset).")
  (draw [_] "Draw body at its position and rotation.")
  (get-body [_] "Get the matter.js body of this player."))
