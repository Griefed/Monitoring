import { reactive, ref } from 'vue';
import { openURL } from "quasar";

const state = reactive({
  autorefresh: true,
  version: ref("dev"),
  updateAvailable: ref(false),
  updateVersion: ref(""),
  updateLink: ref(""),
  updateReminder: ref(true),
  pollingRate: ref(5000),
  particlesCount: ref(40),
  blink: ref(true)
});

const checks = reactive({
  ipRegEx: new RegExp('^\\d+\\.\\d+\\.\\d+\\.\\d+$'),
  addressRegEx: new RegExp('^(https?:\\/\\/)?([0-9a-zA-Z]+[\\-_]?[0-9a-zA-Z]*\\.?)+(\\/\\w+)*|\\d{1,3}.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$'),
  portsRegEx: new RegExp('^\\d{1,5}(,\\d{1,5})*$'),
  numberRegEx: new RegExp('^\\d+$'),
  securityRegEx: new RegExp('^ALL|SETTINGS|DEACTIVATE$'),
  booleanRegEx: new RegExp('^true|false$')
});

let settings = {
  defaultPorts: null,
  additivePorts: null,
  notificationsEnabled: null,
  particlesCount: null,
  pollingRate: null,
  timeoutConnect: null,
  timeoutAvailability: null,
  threadCount: null,
  securitySetting: null
};

let hosts = reactive({
  hosts: []
});

const methods = {
  setSettings(applicationSettings) {
    settings.defaultPorts = applicationSettings.defaultPorts.toString();
    settings.additivePorts = applicationSettings.additivePorts;
    settings.notificationsEnabled = applicationSettings.notificationsEnabled;
    settings.particlesCount = applicationSettings.particlesCount;
    settings.pollingRate = applicationSettings.pollingRate;
    settings.timeoutConnect = applicationSettings.timeoutConnect;
    settings.timeoutAvailability = applicationSettings.timeoutAvailability;
    settings.threadCount = applicationSettings.threadCount;
    settings.securitySetting = applicationSettings.securitySetting;
  },
  setHosts(newHosts) {
    hosts.hosts = [];
    newHosts.forEach(host => {
      hosts.hosts.push(host);
    });
  },
  openInTab(address) {

    if (checks.ipRegEx.test(address)) {
      openURL(
        'http://' + address,
        null,
        {
          noopener: true,
          menubar: false,
          toolbar: false,
          noreferrer: false,
        }
      )
    } else {

      openURL(
        address,
        null,
        {
          noopener: true,
          menubar: false,
          toolbar: false,
          noreferrer: false,
        }
      )

    }
  }
};

export default {
  state,
  checks,
  settings,
  hosts,
  methods
};
