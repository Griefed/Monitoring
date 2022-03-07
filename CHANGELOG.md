### [1.3.2](https://git.griefed.de/Griefed/Monitoring/compare/1.3.1...1.3.2) (2022-03-07)


### 💎 Improvements

* **MainLayout Icon:** Replace icon in layout with animated logo ([4f7523b](https://git.griefed.de/Griefed/Monitoring/commit/4f7523b377e5197520cbb471edc2de553e6a1755))
* **Status Page:** If host name or address exceed certain lengths, truncate them and add a hover-tooltip. ([822db37](https://git.griefed.de/Griefed/Monitoring/commit/822db37c4ecf852a96382c8b580176429e8d7f40))
* **Update Checks:** Periodically check for available updates. Configurable via property de.griefed.monitoring.schedule.updatecheck ([d1d3dd3](https://git.griefed.de/Griefed/Monitoring/commit/d1d3dd3476d36b5397609e1db030c0c876851c17))


### 🦊 CI/CD

* **Releases:** Degrade improv-type to patch-release. ([37213fa](https://git.griefed.de/Griefed/Monitoring/commit/37213fa15d9cf7f544b52111b8e47965bed8c26f))

### [1.3.1](https://git.griefed.de/Griefed/Monitoring/compare/1.3.0...1.3.1) (2022-03-06)


### 🛠 Fixes

* **Startup:** Run initial polling of host information in separate thread so it doesn't block the startup sequence. ([f7bdd8f](https://git.griefed.de/Griefed/Monitoring/commit/f7bdd8f09bbc1bbee8ca4b15ab0ce93a6fe12ee4))
* **Update Checks:** Fix update dialog showing up briefly, even though no update is actually available. ([0498471](https://git.griefed.de/Griefed/Monitoring/commit/0498471da7bcd6925229bd94a2d0de73516736a6))


### Other

* **gitignore:** Add Monitoring-*.jar to ignore list. Nice for testing stuff. ([4251d3b](https://git.griefed.de/Griefed/Monitoring/commit/4251d3bb30def12627e07e24f17c142d2e7b982d))
* **Logging:** Set default logging to INFO level. ([24df16a](https://git.griefed.de/Griefed/Monitoring/commit/24df16adf4679fd44f687c73adb21fa9c7103d29))
* **README:** Update badges ([7e678a6](https://git.griefed.de/Griefed/Monitoring/commit/7e678a64ec78bdfbc285232711ec3cccdaf72d20))
* **README:** Update screenshots in README ([e860c4d](https://git.griefed.de/Griefed/Monitoring/commit/e860c4df9768d531cd80eab4579026d96a810222))

## [1.3.0](https://git.griefed.de/Griefed/Monitoring/compare/1.2.0...1.3.0) (2022-03-06)


### 🚀 Features

* **Update Checker:** Check for available updates. Display popup with information and link to update release page. Update reminders can be disabled by clicking "Don't remind me again". To reactivate, clear cookies. ([6d4911f](https://git.griefed.de/Griefed/Monitoring/commit/6d4911f9fc44a412627faad780d74bb3be8cfe06))

## [1.2.0](https://git.griefed.de/Griefed/Monitoring/compare/1.1.0...1.2.0) (2022-03-05)


### 💎 Improvements

* **Host Availability:** When ping fails, check with socket connection and configurable ports. ([8fed7d4](https://git.griefed.de/Griefed/Monitoring/commit/8fed7d427a441a7314eb6b152b7d918bb4a23664))
* **Host information:** Retrieve host information at startup to prevent multiple pollings running at once. ([c4baed6](https://git.griefed.de/Griefed/Monitoring/commit/c4baed603582b24f446462a2ad58a3604b4835bb))
* **Host information:** When the address is only an IP address matching d+.d+.d+.d+, only check for host availability. ([870cf42](https://git.griefed.de/Griefed/Monitoring/commit/870cf4240ea4ececb8bc4659539330106f6b72d0))
* **Status Page:** Improve visuals and identification of problematic hosts. ([cc057f0](https://git.griefed.de/Griefed/Monitoring/commit/cc057f0cf99e7c40f0a8dc8f16ab2db1983f8c30))


### 📔 Docs

* Add documentation for properties, ports. Add missing param in javadocs. ([069d1f7](https://git.griefed.de/Griefed/Monitoring/commit/069d1f7fbad9b7b037f063da5cf3a0f5b4b3546b))


### 🛠 Fixes

* **Logging:** Split Monitoring log-level so Spring and Monitoring logging have separate log-levels, allowing for more control. ([c8f1c00](https://git.griefed.de/Griefed/Monitoring/commit/c8f1c004c4cfb6271edcfe9a5359af3080847dd0))


### Other

* **deps:** update dependency ghcr.io/griefed/gitlab-ci-cd to v2.0.4 ([e4e269c](https://git.griefed.de/Griefed/Monitoring/commit/e4e269c3a68501d025c33a942ae457333a9b47b3))
* **deps:** update dependency griefed/baseimage-ubuntu-jdk-8 to v2.0.7 ([dab1080](https://git.griefed.de/Griefed/Monitoring/commit/dab1080423ccff237816040d59eb9685412c78ff))
* **README:** Replace image in docker-compose example ([b86c11b](https://git.griefed.de/Griefed/Monitoring/commit/b86c11bd35dca9ec6ad2c1ebb5e0ffe7bed2a5fc))

## [1.1.0](https://git.griefed.de/Griefed/Monitoring/compare/1.0.1...1.1.0) (2022-02-27)


### 💎 Improvements

* **Host Availability:** Depending on hostAvailable, and whether an IP address was acquired, display in green/red whether host is available. ([7611742](https://git.griefed.de/Griefed/Monitoring/commit/7611742bf622d5dcd5ed5df7f4f43e318e380c10))


### 🦊 CI/CD

* **GitHub:** Correctly execute (pre)release actions when tags are pushed. ([6629c13](https://git.griefed.de/Griefed/Monitoring/commit/6629c1304d7e4f554b575ffdaba889669b25103d))
* **Releases:** Create minor release upon improv-commit. ([e45c4db](https://git.griefed.de/Griefed/Monitoring/commit/e45c4db67cab9bbc49eb6bb5f8eb41f5ff57f653))


### Other

* **deps:** update dependency ghcr.io/griefed/gitlab-ci-cd to v2.0.3 ([c68c840](https://git.griefed.de/Griefed/Monitoring/commit/c68c840d76ea7976e6d962b4649d199b89c3798f))
* **deps:** update dependency griefed/baseimage-ubuntu-jdk-8 to v2.0.6 ([196ba0b](https://git.griefed.de/Griefed/Monitoring/commit/196ba0b7eed1760479b820580997aa6abc4f5a0a))
* **deps:** update jamesives/github-pages-deploy-action action to v4.2.5 ([ca4ec47](https://git.griefed.de/Griefed/Monitoring/commit/ca4ec47257116972c0b080438fd37c2ec98fa380))
* **deps:** update plugin edu.sc.seis.launch4j to v2.5.2 ([8bbdc69](https://git.griefed.de/Griefed/Monitoring/commit/8bbdc6964cc95bae2dd409c135a9104af9ee1de9))

### [1.0.1](https://git.griefed.de/Griefed/Monitoring/compare/1.0.0...1.0.1) (2022-02-20)


### 🦊 CI/CD

* Provide default hosts.json for Docker container. Cleanup application-properties. Update README. ([ec30419](https://git.griefed.de/Griefed/Monitoring/commit/ec304195d7d9e3be21bd191a4de5f1edf2ef8617))


### 🛠 Fixes

* Correctly display problematic hosts and those which are okay. Improve visualization of both. ([19bfb09](https://git.griefed.de/Griefed/Monitoring/commit/19bfb09774293268258cf30a3c1f173e01cdb59d))

## [1.0.0](https://git.griefed.de/Griefed/Monitoring/compare/0.11.16...1.0.0) (2022-02-19)


### 🦊 CI/CD

* Add GitHub workflow for GitHub PRs ([c5cb38a](https://git.griefed.de/Griefed/Monitoring/commit/c5cb38a56f18e72e724db498d0c85705bd37160e))
* **Dockerfile:** Remove unnecessary command chaining causing the build to fail. ([9bab116](https://git.griefed.de/Griefed/Monitoring/commit/9bab116906a162cbc8e83a23f4b4309ed7cbc28b))
* **Dockerfile:** Remove unnecessary command chaining causing the build to fail. ([a7ec245](https://git.griefed.de/Griefed/Monitoring/commit/a7ec2455f63a8acc9fe0a694e787d1ff0eaa3115))


### 🧨 Breaking changes!

* Complete rework. Gather information for hosts configured in hosts.json in the base-directory. Display problematic hosts first, others last. No longer acquire system-specific information. ([426fcde](https://git.griefed.de/Griefed/Monitoring/commit/426fcde72c2c42483c1cc34ad8b3ed6c5e6d5130))


### Other

* **deps:** update ghcr.io/griefed/gitlab-ci-cd docker tag to v2.0.2 ([7a7103e](https://git.griefed.de/Griefed/Monitoring/commit/7a7103e4cdd9d2b773896fb62944faee5a6e0c0d))
* **deps:** update griefed/baseimage-ubuntu-jdk-8 docker tag to v2.0.5 ([d914a7c](https://git.griefed.de/Griefed/Monitoring/commit/d914a7c7fcfffd574197128f0c29eb163c453d21))

### [0.11.16](https://git.griefed.de/Griefed/Monitoring/compare/0.11.15...0.11.16) (2021-12-29)


### 🦊 CI/CD

* Add jacoco coverage and dependencies scanning. Whoops :) ([dc4cdc5](https://git.griefed.de/Griefed/Monitoring/commit/dc4cdc56ebc5e0fa11ab80d2e023ae96bf06f48b))
* Fix username acquisition ([cc3c955](https://git.griefed.de/Griefed/Monitoring/commit/cc3c955af256599de3e6f3f25d749d96f3c4d7f0))
* Rework CI/CD configuration for GitLab, GitHub and DockerHub. ([9a0d6b5](https://git.griefed.de/Griefed/Monitoring/commit/9a0d6b5c23176434ecc6281db3dc332f513c53d8))
* Update renovate-config ([1697c59](https://git.griefed.de/Griefed/Monitoring/commit/1697c598cc79e9d755098fd474be119735a4b034))


### Other

* Add funding stuffs ([8fb2457](https://git.griefed.de/Griefed/Monitoring/commit/8fb2457943870cc46cae4b494155be97322734f8))
* Bring README somewhat up to date with other repos ([43166a8](https://git.griefed.de/Griefed/Monitoring/commit/43166a8f7d6a80680692cc708b63a22f1b7276f1))
* **deps:** add renovate.json ([b7a17cf](https://git.griefed.de/Griefed/Monitoring/commit/b7a17cfea537f5108018614c173c248f1407910a))
* **deps:** pin dependencies ([b870a28](https://git.griefed.de/Griefed/Monitoring/commit/b870a289cf17ec437c0716b3ab65f09328336c71))
* **deps:** update dependency @babel/eslint-parser to v7.16.5 ([9a79455](https://git.griefed.de/Griefed/Monitoring/commit/9a79455be115c7dae1381c6ff8e2e99bbb992a95))
* **deps:** update dependency @quasar/app to v3.2.6 ([5663b34](https://git.griefed.de/Griefed/Monitoring/commit/5663b344a95b7305bd159184cb8482f10f3b6ad3))
* **deps:** update dependency com.github.oshi:oshi-core to v5.8.6 ([6ac0775](https://git.griefed.de/Griefed/Monitoring/commit/6ac077551010f80a714ba22dda8fe168c4606b87))
* **deps:** update dependency core-js to v3.20.1 ([a8e1d73](https://git.griefed.de/Griefed/Monitoring/commit/a8e1d730f0fc30fc7371bfc1e130a0d68f21d999))
* **deps:** update dependency eslint to v8.5.0 ([bb907c4](https://git.griefed.de/Griefed/Monitoring/commit/bb907c487f1bafb9c3bda1a0ba5bfa62ebc29f58))
* **deps:** update dependency gradle to v7.3.3 ([8cb57a1](https://git.griefed.de/Griefed/Monitoring/commit/8cb57a12b7851f27013d2ac2991e97a2c7aad8ec))
* **deps:** update dependency quasar to v2.4.2 ([ba0b9a3](https://git.griefed.de/Griefed/Monitoring/commit/ba0b9a3cb03aeae1b76d20e9122e462baab1cacf))
* **deps:** update jamesives/github-pages-deploy-action action to v4.1.8 ([d612315](https://git.griefed.de/Griefed/Monitoring/commit/d612315efbe5decb2dd9b88356f1231cf9390b6b))
* **deps:** update npm to v8 ([27e32e3](https://git.griefed.de/Griefed/Monitoring/commit/27e32e3302c1daa8512280588b846d46311c4e15))
* **deps:** update spring boot to v2.6.2 ([6747c29](https://git.griefed.de/Griefed/Monitoring/commit/6747c29f2e281cce7d201287fa2b938bd04d5939))
