(ns status-im.ui.screens.wallet.send.redesigned-views
  (:require-macros [status-im.utils.views :refer [defview letsubs]])
  (:require [status-im.ui.screens.wallet.send.views.amount :as amount]
            [status-im.ui.screens.wallet.send.views.overview :as overview]
            [status-im.ui.screens.wallet.send.views.recipient :as recipient]
            [status-im.utils.ethereum.tokens :as tokens]
            [status-im.utils.ethereum.core :as ethereum]
            [taoensso.timbre :as log]
            [reagent.core :as reagent]
            [status-im.constants :as constants]))

(defview choose-amount-token []
  (letsubs [{:keys [transaction modal? contact native-currency]} [:get-screen-params :wallet-choose-amount]
            balance       [:balance]
            prices        [:prices]
            network       [:account/network]
            all-tokens    [:wallet/all-tokens]
            fiat-currency [:wallet/currency]
            web3          [:get :web3]]
    (let [chain (ethereum/network->chain-keyword network)]
      [amount/render-choose-amount {:web3            web3
                                    :balance         balance
                                    :network         network
                                    :chain           chain
                                    :all-tokens      all-tokens
                                    :modal?          modal?
                                    :prices          prices
                                    :native-currency native-currency
                                    :fiat-currency   fiat-currency
                                    :contact         contact
                                    :transaction     transaction}])))

(defview transaction-overview []
  (letsubs [{:keys [transaction flow contact]} [:get-screen-params :wallet-txn-overview]
            prices        [:prices]
            network       [:account/network]
            all-tokens    [:wallet/all-tokens]
            fiat-currency [:wallet/currency]
            web3          [:get :web3]]
    (let [chain (ethereum/network->chain-keyword network)
          native-currency (tokens/native-currency chain)
          token (tokens/asset-for all-tokens
                                  (ethereum/network->chain-keyword network) (:symbol transaction))]
      [overview/render-transaction-overview {:transaction     transaction
                                             :flow            flow
                                             :contact         contact
                                             :prices          prices
                                             :network         network
                                             :token           token
                                             :native-currency native-currency
                                             :fiat-currency   fiat-currency
                                             :all-tokens      all-tokens
                                             :chain           chain
                                             :web3            web3}])))

;; MAIN SEND TRANSACTION VIEW
(defn- send-transaction-view [opts]
  [recipient/render-choose-recipient opts])

(def default-transaction
  {:optimal-gas ethereum/default-transaction-gas
   :method      constants/web3-send-transaction
   :symbol      :ETH})

;; SEND TRANSACTION FROM WALLET
(defview send-transaction []
  (letsubs [network        [:account/network]
            network-status [:network-status]
            all-tokens     [:wallet/all-tokens]
            contacts       [:contacts/active]
            web3           [:get :web3]]
    [send-transaction-view {:modal?         false
                            :transaction    default-transaction
                            :web3           web3
                            :network        network
                            :all-tokens     all-tokens
                            :contacts       contacts
                            :network-status network-status}]))

;; SEND TRANSACTION FROM DAPP
(defview send-transaction-modal []
  (letsubs [{:keys [transaction flow contact]} [:get-screen-params :wallet-send-transaction-modal]
            prices [:prices]
            network [:account/network]
            all-tokens [:wallet/all-tokens]
            web3 [:get :web3]
            fiat-currency [:wallet/currency]]
    (let [chain (ethereum/network->chain-keyword network)
          native-currency (tokens/native-currency chain)
          token (tokens/asset-for all-tokens
                                  (ethereum/network->chain-keyword network) (:symbol transaction))]
      [overview/render-transaction-overview {:transaction     transaction
                                             :flow            flow
                                             :contact         contact
                                             :prices          prices
                                             :network         network
                                             :token           token
                                             :web3            web3
                                             :native-currency native-currency
                                             :fiat-currency   fiat-currency
                                             :all-tokens      all-tokens
                                             :chain           chain}])))