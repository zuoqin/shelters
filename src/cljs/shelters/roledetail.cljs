(ns shelters.roledetail
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [secretary.core :as sec :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [shelters.core :as shelters]
            [ajax.core :refer [GET POST PUT DELETE]]
            [clojure.string :as str]
            [om-bootstrap.button :as b]
            [om-bootstrap.panel :as p]
            [om.dom :as omdom :include-macros true]
            [cljs.core.async :refer [put! dropping-buffer chan take! <!]]
            [om-bootstrap.input :as i]
            [cljs-time.core :as tm]
            [cljs-time.format :as tf]
            [shelters.settings :as settings]
  )
  (:import goog.History)
)

(def jquery (js* "$"))

(enable-console-print!)

(def ch (chan (dropping-buffer 2)))
(defonce app-state (atom  {:role {} :isinsert false :view 1 :current "Role Detail"} ))

(defn handleChange [e]
  ;(.log js/console e  )  
  ;(.log js/console "The change ....")
  (swap! app-state assoc-in [:role (keyword (.. e -nativeEvent -target -id))] (.. e -nativeEvent -target -value))
)


(defn OnDeleteRoleError [response]
  (let [     
      newdata {:roleid (get response (keyword "tripid") ) }
    ]

  )
  ;; TO-DO: Delete Role from Core
  ;;(.log js/console (str  (get (first response)  "Title") ))
)


(defn OnDeleteRoleSuccess [response]
  (let [
      roles (:roles @shelters/app-state)
      newroles (remove (fn [role] (if (= (:role role) (:role @app-state) ) true false  )) roles)
    ]
    ;(swap! sbercore/app-state assoc-in [:token] newdata )
    (swap! shelters/app-state assoc-in [:roles] newroles)
  )

    (-> js/document
      .-location
      (set! "#/roles"))
)

(defn OnUpdateRoleError [response]
  (let [     
      ;newdata {:tripid (get response (keyword "tripid") ) }
      tr1 (.log js/console (str "In OnUpdateRoleError " response))
    ]
  )
  ;; TO-DO: Delete Trip from Core
  ;;(.log js/console (str  (get (first response)  "Title") ))
)


(defn OnUpdateRoleSuccess [response]
  (let [
      roles (:roles @shelters/app-state)
      delrole (remove (fn [role] (if (= (:id role) (:id (:role @app-state))) true false)) roles)
      addrole (conj delrole {:id (:id (:role @app-state)) :name (:name (:role @app-state)) :description (:description (:role @app-state)) :level (:level (:role @app-state))})

      tr1 (.log js/console (str "In OnUpdateRoleSuccess " response))
    ]
    (swap! shelters/app-state assoc-in [:roles] addrole)

    (-> js/document
      .-location
      (set! "#/roles"))
  )
)


(defn deleteRole [role]
  (DELETE (str settings/apipath  "api/user?login=" role) {
    :handler OnDeleteRoleSuccess
    :error-handler OnDeleteRoleError
    :headers {
      :content-type "application/json" 
      :Authorization (str "Bearer "  (:token (:token @shelters/app-state)))}
    :format :json})
)



(defn updateRole []
  (let [
    tr1 (.log js/console (str "In updateRole"))
    ]
    (PUT (str settings/apipath  "updateRole") {
      :handler OnUpdateRoleSuccess
      :error-handler OnUpdateRoleError
      :headers {
        :token (str (:token (:token @shelters/app-state)))}
      :format :json
      :params {:roleName (:name (:role @app-state)) :roleLevel (:level (:role @app-state)) :roleId (:id (:role @app-state)) :roleDescription (:description (:role @app-state)) }})
  )
)


(defn OnCreateRoleError [response]
  (let [     
      newdata {:tripid (get response (keyword "tripid") ) }
    ]

  )
  ;; TO-DO: Delete Trip from Core
  ;;(.log js/console (str  (get (first response)  "Title") ))
)

(defn map-role [role]
  (let [
    id (get role "roleId")
    level (get role "roleLevel")
    description (get role "roleDescription")
    rolename (get role "roleName")
    ;tr1 (.log js/console (str  "username=" username ))
    result {:id id :name rolename :level level :description description}
    ]
    ;
    result
  )
)

(defn OnCreateRoleSuccess [response]
  (let [
      role (map-role response)
      roles (:roles @shelters/app-state)
      addrole (conj roles {:id (:id role) :name (:name role) :description (:description role) :level (:level role)}) 
    ]
    (swap! shelters/app-state assoc-in [:roles] addrole)

    (-> js/document
      .-location
      (set! "#/roles"))

  )
)

(defn createRole []
  (let [
    tr1 (.log js/console (str "In updateRole"))
    ]
    (POST (str settings/apipath  "addRole") {
      :handler OnCreateRoleSuccess
      :error-handler OnCreateRoleError
      :headers {
        :token (str (:token (:token @shelters/app-state)))}
      :format :json
      :params { :roleName (:name (:role @app-state)) :roleLevel (:level (:role @app-state)) :roleDescription (:description (:role @app-state))}})
  )
)


(defn onDropDownChange [id value]
  ;(.log js/console () e)
  (swap! app-state assoc-in [:role] value) 
)


(defn setRolesDropDown []
  (jquery
     (fn []
       (-> (jquery "#roles" )
         (.selectpicker {})
       )
     )
   )
   (jquery
     (fn []
       (-> (jquery "#roles" )
         (.selectpicker "val" (:role @app-state))
         (.on "change"
           (fn [e]
             (
               onDropDownChange (.. e -target -id) (.. e -target -value)
             )
           )
         )
       )
     )
   )
)


(defn setNewRoleValue [key val]
  (swap! app-state assoc-in [(keyword key)] val)
)

(defn setRole []
  (let [
    ;roles (:roles @shelters/app-state)        
    role (first (filter (fn [role] (if (= (:name @app-state) (:name role)  )  true false)) (:roles @shelters/app-state )))

    ;tr1 (.log js/console (str "role=" role))
      
    ]
    (swap! app-state assoc-in [:name ]  (:name role) ) 
    (swap! app-state assoc-in [:description] (:description role) )
  )
)


(defn setcontrols [value]
  (case value
    46 (setRole)
  )
)


(defn initqueue []
  (doseq [n (range 1000)]
    (go ;(while true)
      (take! ch(
        fn [v] (
           ;.log js/console v
           ;(setcalculatedfields) 
           setcontrols v
           
           ;(.log js/console v)  
          )
        )
      )
    )
  )
)


(initqueue)

(defn array-to-string [element]
  (let [
      newdata {:empname (get element "empname") } 
    ]
    (:empname newdata)
  )
)




(defn OnError [response]
  (let [     
      newdata { :error (get (:response response)  "error") }
    ]
    (.log js/console (str  response )) 
    
  )
  
  
)


(defn getRoleDetail []
  ;(.log js/console (str "token: " " " (:token  (first (:token @t5pcore/app-state)))       ))
  (if
    (and 
      (not= (:name @app-state) nil)
      (not= (:name @app-state) "")
    )
    (setRole)
  )
)

(defn handleFromChange [e]
  ;;(.log js/console e  )  
  (.log js/console "The change ....")

)


(defn onMount [data]
  (swap! app-state assoc-in [:current] 
    "Role Detail"
  )
  (getRoleDetail)
  (put! ch 46)
  (swap! shelters/app-state assoc-in [:view] 6)
)


(defn handle-change [e owner]
  (.log js/console () e)
  (swap! app-state assoc-in [:role (keyword (.. e -target -id))] 
    (.. e -target -value)
  ) 
)


(defn buildRolesList [data owner]
  (map
    (fn [text]
      (dom/option {:key (:name text) :value (:name text)
                    :onChange #(handle-change % owner)} (:name text))
    )
    (:roles @app-state )
  )
)



(defcomponent roledetail-page-view [data owner]
  (did-mount [_]
    (onMount data)
  )
  (did-update [this prev-props prev-state]
    (.log js/console "Update happened") 
  )
  (render
    [_]
    (let [style {:style {:margin "10px;" :padding-bottom "0px;"}}
      ;tr1 (.log js/console (str "name= " @data))
      ]
      (dom/div {:style {:padding-top "70px"}}
        (om/build shelters/website-view shelters/app-state {})
        (dom/div {:id "user-detail-container"}
          (dom/span
            (dom/div {:className "panel panel-default"  :id "divUserInfo"}
              
              (dom/div {:className "panel-heading"}
                (dom/h5 "Name: " 
                  (dom/input {:id "name" :type "text" :onChange (fn [e] (handleChange e)) :value (:name (:role @data))})

                )
                
                (dom/h5 "Description: "
                  (dom/input {:id "description" :type "text" :onChange (fn [e] (handleChange e)) :value (:description (:role @data))})
                )

                (dom/h5 "Level: "
                  (dom/input {:id "level" :type "number" :onChange (fn [e] (handleChange e)) :value (:level (:role @data))})
                )
              )
            )
          )
        )
        (dom/nav {:className "navbar navbar-default" :role "navigation"}
          (dom/div {:className "navbar-header"}
            (b/button {:className "btn btn-default" :onClick (fn [e] (if (:isinsert @app-state) (createRole) (updateRole)) )} (if (:isinsert @app-state) "Insert" "Update"))
            (b/button {:className "btn btn-danger" :style {:visibility (if (:isinsert @app-state) "hidden" "visible")} :onClick (fn [e] (deleteRole (:name @app-state)))} "Delete")

            (b/button {:className "btn btn-info"
              :onClick (fn [e]
                (js/window.history.back)
              )} "Cancel"
            )
          )
        )
      )
    )

  )
)



(sec/defroute roledetail-page "/roledetail/:id" {id :id}
  (let [
    role (first (filter (fn [x] (if (= id (:id x)) true false)) (:roles @shelters/app-state)))

    tr1 (swap! app-state assoc-in [:role] role)
    ]

    (swap! app-state assoc-in [:isinsert]  false)
    ;(setRole)
    (om/root roledetail-page-view
             app-state
             {:target (. js/document (getElementById "app"))})

  )
)


(sec/defroute roledetail-new-page "/roledetail" {}
  (
    (swap! app-state assoc-in [:role] {:name "" :description "" :level 0}) 
    (swap! app-state assoc-in [:isinsert]  true )
 
    ;(swap! app-state assoc-in [:role ]  "role" ) 
    ;(swap! app-state assoc-in [:password] "" )


    (om/root roledetail-page-view
             app-state
             {:target (. js/document (getElementById "app"))})
  )
)
