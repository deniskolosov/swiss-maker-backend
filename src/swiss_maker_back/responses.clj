(ns swiss-maker-back.responses
  (:require [environ.core :refer [env]])
  (:require [spec-tools.data-spec :as ds]))


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

(def pair
  {:pairing/id            int?
   :pairing/white-id      string?
   :pairing/black-id      string?
   :pairing/board-no      int?
   :pairing/result        float?
   :pairing/tournament-id int?
   :pairing/round-no      int?})

(def pairing
  {:pairing [pair]})

(def tournaments
{:tournaments [tournament]})

(def players
{:players [player]})
