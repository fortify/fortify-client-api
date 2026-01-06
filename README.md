# Fortify Client API Libraries 


<!-- START-INCLUDE:p.marketing-intro.md -->

[Fortify Application Security](https://www.microfocus.com/en-us/solutions/application-security) provides your team with solutions to empower [DevSecOps](https://www.microfocus.com/en-us/cyberres/use-cases/devsecops) practices, enable [cloud transformation](https://www.microfocus.com/en-us/cyberres/use-cases/cloud-transformation), and secure your [software supply chain](https://www.microfocus.com/en-us/cyberres/use-cases/securing-the-software-supply-chain). As the sole Code Security solution with over two decades of expertise and acknowledged as a market leader by all major analysts, Fortify delivers the most adaptable, precise, and scalable AppSec platform available, supporting the breadth of tech you use and integrated into your preferred toolchain. We firmly believe that your great code [demands great security](https://www.microfocus.com/cyberres/application-security/developer-security), and with Fortify, go beyond 'check the box' security to achieve that.

<!-- END-INCLUDE:p.marketing-intro.md -->



<!-- START-INCLUDE:repo-intro.md -->

This repository contains various modules for interacting with Fortify products through their respective REST API's. This is by no means meant to act like an official Fortify client SDK; its primary purpose is to provide shared libraries for use by Fortify-provided integration utilities. Use of these libraries in 3<sup>rd</sup>-party utilities is neither endorsed nor recommended. In particular, please note the following before considering using `fortify-client-api` in any application:

* There is no guarantee that any functionality provided by `fortify-client-api` actually works; functionality is only tested indirectly through the various integration utilities that utilize `fortify-client-api`
* `fortify-client-api` only covers a subset of the API's provided by the various Fortify products, as required by the various integration utilities
* New versions of `fortify-client-api` may introduce significant changes without taking backward compatibility into account, and existing functionality may cease to exist; upgrading to a new version of `fortify-client-api` may require a significant rewrite of code dependent on `fortify-client-api`
* No maintenance, including bug fixes, is being done on older versions of `fortify-client-api`
* Feature requests are not accepted
* Bug fixes are only considered if a bug affects any of the Fortify-provided integration utilities

<!-- END-INCLUDE:repo-intro.md -->


## Resources


<!-- START-INCLUDE:repo-resources.md -->

* **Usage**: [USAGE.md](USAGE.md)
* **Source code**: https://github.com/fortify/fortify-client-api
* **Automated builds**: https://github.com/fortify/fortify-client-api/actions
* **Maven Repositories**
    * **Releases**: https://repo1.maven.org/maven2/ 
    * **Snapshots**: https://s01.oss.sonatype.org/content/repositories/snapshots/
* **Sample Projects using fortify-client-api**
  * https://github.com/fortify-ps/FortifyBugTrackerUtility
  * https://github.com/fortify-ps/FortifySyncFoDToSSC
  * https://github.com/fortify-ps/fortify-integration-maven-webinspect 
* **Contributing Guidelines**: [CONTRIBUTING.md](CONTRIBUTING.md)
* **Code of Conduct**: [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
* **License**: [LICENSE.txt](LICENSE.txt)

<!-- END-INCLUDE:repo-resources.md -->



<!-- START-INCLUDE:h2.support.md -->

## Support

For general assistance, please join the [Fortify Community](https://community.opentext.com/cybersec/fortify/) to get tips and tricks from other users and the OpenText team.
 
OpenText customers can contact our world-class [support team](https://www.opentext.com/support/opentext-enterprise/) for questions, enhancement requests and bug reports. You can also raise questions and issues through your OpenText Fortify representative like Customer Success Manager or Technical Account Manager if applicable.

You may also consider raising questions or issues through the [GitHub Issues page](https://github.com/fortify/fortify-client-api/issues) (if available for this repository), providing public visibility and allowing anyone (including all contributors) to review and comment on your question or issue. Note that this requires a GitHub account, and given public visibility, you should refrain from posting any confidential data through this channel. 

<!-- END-INCLUDE:h2.support.md -->


---

*[This document was auto-generated from README.template.md; do not edit by hand](https://github.com/fortify/shared-doc-resources/blob/main/USAGE.md)*
