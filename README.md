## WhereAmI - JavaServer

### Motivation

This is a free platform with the goal of helping researchers to test and develop indoor tracking algorithms using Wi-Fi signal information. Apart of the server, we also build an Android application to help user to collect raw data more easily.

### Table Of Contents

* [Data Model & Lifecycle](#data-model--lifecycle) 
* [How to become a Algorithm Provider](#how-to-become-a-algorithm-provider)
* [REST API Docs](https://whereami.nunum.me/docs/swagger)
* [ava API Docs](https://whereami.nunum.me/docs/java)
* [FAQ](#faq)

### Data Model & Lifecycle


This is a brief summary of the entities. Let's begin with **Localization**. This entity represents a point on the map, like your home in a satellite view. Within your home, you have various rooms, there are **Positions**. With this, we have the relationship of **one localization** can have **zero or more positions**. The localizations can be private for the device that created it. In other words, you can share with other users your localizations. The privacy is declared at its creation.


A **Training** entity is created with the desired algorithm and the respective provider. An **Algorithm** can be proposed by everyone. This is because several people like more reading than implementing and vice versa, thus **the same algorithm** can be provided by several users and their results can be compared and competition may also be resulted by this design.

An **AlgorithmProvider** can be, for now, either a user with their own server or a user that has a git repository ready to be deployed. Both must apply to be a provider. This is just e email verification and there are ready to go.

The provider with their own server will receive all fingerprints for the **Training requests**  via HTTP. Remember that a Training request belongs to a localization that has multiples positions and each localization have hundreds of Wi-Fi fingerprints. When you train the model, you must update the respective **Training** to inform that you are ready to predict the user localization. After that, your server will be queried. From this description, the server owner registers as to be an algorithm provider by giving two required HTTP resources, one for data ingestion, and another for the prediction phase. For the git owners, we integrate the repository with the platform.


When exists a training request for a given provider, the subsequent training request, will reactivate the sinking of the data to the HTTP provider, the cursor is preserved. In other words, your server will receive only newer data. 


The visible **Localizations** and **Positions** can be reported as **Spam** by sanity.


To conclude this chapter, the use case that this platform offers is the integration of two actors, a user who shares Wi-Fi information and a Provider who predicts the user localization using their algorithm implementation. 

### How to become a Algorithm provider

Open the swagger API, find the resource **provider**, make a POST request by filling your email and wait for it in your mailbox. click on the link and voila. You can use the **provider** sub-resource of **algorithm** to tell what implementations you have to offer.

### FAQ

What is a Wi-Fi fingerprint?

[Wikipedia has better](https://en.wikipedia.org/wiki/Wi-Fi_positioning_system#Fingerprinting_based)
