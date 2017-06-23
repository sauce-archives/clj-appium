(ns clj-appium.basic-test-setup-android
  (:require [clj-appium.core :as appium]
            [clojure.test :refer :all]))


(def driver (atom nil))


(defn before
  []
  (let [caps (appium/create-desired-caps :testobject_api_key "YOUR_API_KEY"
                                         :testobject_device "DEVICE_NAME"
                                         :testobject_appium_version "APPIUM_VERSION"
                                         :testobject_cache_device false)]
    (reset! driver (appium/create-driver :android "APPIUM_URL" caps))))


(defn after
  []
  (println (appium/get-cap-val @driver "testobject_test_report_url"))
  (println (appium/get-cap-val @driver "testobject_test_live_view_url") "\n")
  (when @driver
    (appium/quit-driver @driver)))


(defn fixture-each [f]
  (before)
  (f)
  (after))


(use-fixtures :each fixture-each)


(def elements [{:by :id :val "net.ludeke.calculator:id/digit0" :name "zero"}
               {:by :id :val "net.ludeke.calculator:id/digit2" :name "two"}
               {:by :id :val "net.ludeke.calculator:id/div" :name "div"}
               {:by :id :val "net.ludeke.calculator:id/plus" :name "plus"}
               {:by :id :val "net.ludeke.calculator:id/equal" :name "equal"}
               {:by :xpath :val "//android.widget.EditText[1]" :name "result-field"}])


(defn- get-element
  [k]
  (some #(when (= (:name %) k) (appium/find-element % @driver)) elements))


(deftest two-plus-two-operation
  (is (let [click-order [(get-element "two") (get-element "plus") (get-element "two") (get-element "equal")]]
        (appium/click click-order)
        (appium/wait-text-to-be-present @driver 30 (get-element "result-field") "4"))))


(deftest zeros-division-operation
  (is (let [click-order [(get-element "zero") (get-element "div") (get-element "zero") (get-element "equal")]]
        (appium/click click-order)
        (appium/wait-text-to-be-present @driver 30 (get-element "result-field") "NaN"))))


(run-tests)