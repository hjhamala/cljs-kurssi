(ns widgetshop.error-page
  (:require
    [reagent.core :as r]
    [cljsjs.material-ui]
    [cljs-react-material-ui.core :refer [get-mui-theme color]]
    [cljs-react-material-ui.reagent :as material-ui]
    [cljs-react-material-ui.icons :as ic]
    [widgetshop.app.state :as state]
    [reagent.core :as reagent]

    [widgetshop.app.ui :as ui]
    [widgetshop.app.user :as user]
    [widgetshop.db :as db]
    [cljs.spec.alpha :as s]))

(defn get-page
  [app]
  [material-ui/paper
   [:h1 "Error!!!!"]])
