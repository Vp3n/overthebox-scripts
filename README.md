Utilities for [overthebox router!](https://www.ovhtelecom.fr/overthebox/)

# Overview

Overthebox allows to aggregate multiple connection with multiplexing. 
You can aggregate 4G with classic ADSL for example.
 
Each connection is represented by an interface in the router UI, 
you can limit the bandwith or apply a QoS if you want.
 
I missed one feature that is deciding to activate/deactivate one interface
automatically based on the hour of the day. 
It's a simple way to avoid consuming all your 4G data plan, download things
(maybe more slowly) at night on the ADSL fallback, and make sure 4G is 
disabled. 

This project solve exactly this : it's a simple script with 2 options : 
- Start an interface
- Stop an interface

With that it's dead easy to make a cron to fix the problem. 

# How to use it

The project use [sbt native packager!](https://github.com/sbt/sbt-native-packager)
so can package it how it's best for you.

the script requires the parameters below to work :

- `-Dapp.overthebox.action=Stop` (`Start` | `Stop`)
- `-Dapp.overthebox.interface=` (interface name in overthebox)
- `-Dapp.overthebox.login=` 
- `-Dapp.overthebox.password=` 

You can also set them through environnement variables if needed : 

- `OVERTHEBOX_LOGIN`
- `OVERTHEBOX_PASSWORD`
- `OVERTHEBOX_ACTION`
- `OVERTHEBOX_INTERFACE`

If you need to reach your overthebox through a custom domain or IP you can use

-  `-Dapp.overthebox.domain=`

or
 
- `OVERTHEBOX_DOMAIN`

The default value being `overthebox.ovh`

# FAQ 

- Why Scala + all those dependencies ? 

Because ? Sure it's very likely the most heavyweight script in the world
(around ~23MB) but it was fast and simple to write and solve a personal problem :)

Feel free to impl it with anything else.


# License 

MIT