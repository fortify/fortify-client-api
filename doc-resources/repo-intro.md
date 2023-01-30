This repository contains various modules for interacting with Fortify products through their respective REST API's. This is by no means meant to act like an official Fortify client SDK; its primary purpose is to provide shared libraries for use by Fortify-provided integration utilities. Use of these libraries in 3<sup>rd</sup>-party utilities is neither endorsed nor recommended. In particular, please note the following before considering using `fortify-client-api` in any application:

* There is no guarantee that any functionality provided by `fortify-client-api` actually works; functionality is only tested indirectly through the various integration utilities that utilize `fortify-client-api`
* `fortify-client-api` only covers a subset of the API's provided by the various Fortify products, as required by the various integration utilities
* New versions of `fortify-client-api` may introduce significant changes without taking backward compatibility into account, and existing functionality may cease to exist; upgrading to a new version of `fortify-client-api` may require a significant rewrite of code dependent on `fortify-client-api`
* No maintenance, including bug fixes, is being done on older versions of `fortify-client-api`
* Feature requests are not accepted
* Bug fixes are only considered if a bug affects any of the Fortify-provided integration utilities
