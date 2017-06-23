(ns clj-appium.basic-test-setup-ios
  (:require [clj-appium.core :as appium]
            [clojure.test :refer :all]))


(def driver (atom nil))


(defn before
  []
  (let [caps (appium/create-desired-caps :testobject_api_key "YOUR_API_KEY"
                                         :testobject_device "DEVICE_NAME"
                                         :testobject_appium_version "APPIUM_VERSION"
                                         :testobject_cache_device false)]
    (reset! driver (appium/create-driver :ios "APPIUM_URL" caps))))


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


(deftest open-webpage-and-take-screenshot
  (is (do
        (appium/implicitly-wait @driver)
        (Thread/sleep 10000)
        (appium/get-url @driver "http://www.amazon.com")
        (appium/rotate @driver :landscape)
        (Thread/sleep 1000)
        (appium/rotate @driver :portrait)
        (appium/get-screenshot-as @driver :file)
        true)))


(run-tests)