(ns swiss-maker-back.responses
  (:require [environ.core :refer [env]]))


(def base-url (or (env :swiss-maker-base-url) "localhost:3000"))

(def tournament
  {:tournament/id int?
   :tournament/name string?
   :tournament/num_of_rounds int?
   :tournament/current_round int?})

(def player
  {:player/id            string?
   :player/name          string?
   :player/rating        int?
   :player/current-score int?
   :player/tournament-id int?})

(def tournaments
  {:tournaments [tournament]})

(def players
  {:players [player]})
