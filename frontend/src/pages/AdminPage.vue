<template>
  <q-page>
    <div class="fullscreen no-wrap row">

      <!-- TODO Put into splitter vertical -->
      <!-- TODO log cards or whatever they can best be wrapped im -->
      <q-splitter
        v-model="splitterModel"
        :after-class="this.$q.dark.isActive ? 'background-dark' : 'background'"
        :limits="[0, 100]"
        before-class=""
        separator-style="background: #c0ffee;"
        style="height: 100%; width: 100%; position: absolute; right: 0; margin-right: -5px;"
      >
        <template v-slot:before>
          <div class="column full-height">

            <q-card class="monitoring">

              <q-card-section class="text-center">
                <b class="text-weight-bolder text-h4">Monitoring</b>
              </q-card-section>

              <q-card-section class="text-left">
                <b>Current version: </b>{{ this.store.state.version }}<br>
                <b>Latest version: </b>{{ this.store.state.updateVersion }}
              </q-card-section>

              <q-card-section class="card align-bottom">
                <SettingsEditor/>

                <q-separator color="secondary" inset
                             style="margin-bottom: 10px; margin-top: 10px;"/>

                <div class="row no-wrap">
                  <q-btn
                    color="primary"
                    label="Save"
                    style="width: 50%; margin-right: 10px;"
                    @click="saveSettings"
                  />

                  <q-btn
                    color="accent"
                    label="Reload"
                    style="width: 50%; margin-left: 10px;"
                    @click="loadSettings"
                  />
                </div>
                <div class="row no-wrap">
                  <q-btn
                    color="accent"
                    label="Export"
                    style="width: 100%; margin-top: 10px;"
                  />
                </div>
              </q-card-section>

            </q-card>

          </div>
        </template>

        <template v-slot:separator>
          <q-btn
            class="flex-center"
            color="primary"
            dense
            icon="drag_indicator"
            round
            size="20px"
            text-color="white"
          />
        </template>

        <template v-slot:after>
          <div class="column full-height" style="padding-right: 5px; width: 100%">
            <HostsEditor/>
          </div>
        </template>
      </q-splitter>

    </div>

  </q-page>
</template>

<script>
import {defineComponent, inject, ref} from 'vue';
import SettingsEditor from "components/SettingsEditor";
import HostsEditor from "components/HostsEditor";
import axios from 'axios';

export default defineComponent({
  name: 'AdminPage',
  components: {HostsEditor, SettingsEditor},
  setup() {

    const store = inject('store');

    let settings = ref([]);
    let hosts = ref([]);

    return {
      store,
      settings,
      hosts,
      splitterModel: ref(39),
      downThumbStyle: {
        right: '4px',
        borderRadius: '5px',
        backgroundColor: '#325358',
        width: '5px',
        opacity: 0.75
      },

      downBarStyle: {
        right: '2px',
        borderRadius: '9px',
        backgroundColor: '#c0ffee',
        width: '9px',
        opacity: 0.3
      }
    }
  },
  methods: {
    showSettings() {

      console.log(this.store.settings)
    },
    saveSettings() {

      let settingsResults = this.validateSettings(this.store.settings);
      let hostsResults = this.validateHosts(this.store.hosts.hosts);

      if (settingsResults.length > 0 || hostsResults.length > 0) {

        let errorMessage = "<span class='headline text-weight-bolder'>Your configuration has errors:</span>";
        if (settingsResults.length > 0) {

          errorMessage = errorMessage
            + "<br><span class='sub-headline text-weight-bolder'>Settings:</span><br><ol>";

          settingsResults.forEach(error => {

            let errorObject = JSON.parse(error);

            errorMessage = errorMessage + "<li>" + errorObject.setting + ":"
              + errorObject.erroringField + "<br>Reason: " + errorObject.reason + '</li>';
          });

          errorMessage = errorMessage + '</ol>';
        }

        if (hostsResults.length > 0) {

          errorMessage = errorMessage
            + "<br><span class='sub-headline text-weight-bolder'>Hosts:</span><ul>";

          hostsResults.forEach(error => {

            let errorObject = JSON.parse(error);

            errorMessage = errorMessage + '<li><span class=\'text-weight-bolder\'>Host: '
              + errorObject.hostIndex + '</span><ol>';

            errorObject.errors.forEach(hostError => {

              errorMessage = errorMessage + "<li>" + hostError.setting + ": "
                + hostError.erroringField + "</br>" + hostError.reason + '</li>';
            });

            errorMessage = errorMessage + '</ol></li>';

          });

          errorMessage = errorMessage + '</ul>';

        }

        this.$q.notify({
          position: 'center',
          caption: 'You must fix these before continuing.',
          color: 'negative',
          html: true,
          message: errorMessage
        });

      } else {

        var config = {
          settings: this.store.settings,
          hosts: this.store.hosts.hosts
        }

        this.$admin.put('/', config
        ).then(response => {
          console.log('axios');
          console.log(response);
        }).catch(error => {
          console.log(error);
          this.$q.notify({
            position: 'center',
            message: 'Error saving configuration. ' + error,
            color: 'negative',
            timeout: 5000
          });
        });
      }
    },
    validateSettings(settings) {
      let result = [];

      if (!this.store.checks.portsRegEx.test(settings.defaultPorts) || !this.checkPorts(
        settings.defaultPorts)) {
        result.push('{"setting": "Default Ports","erroringField": "' + settings.defaultPorts
          + '","reason": "Ports must be numbers, separated by a comma, only. Each port must not be greater than 65535."}');
      }
      if (!this.store.checks.numberRegEx.test(settings.particlesCount)) {
        result.push('{"setting": "Particles Amount","erroringField": "' + settings.particlesCount
          + '","reason": "Numbers only."}');
      }
      if (!this.store.checks.numberRegEx.test(settings.timeoutConnect)) {
        result.push('{"setting": "Connection Timout","erroringField": "' + settings.timeoutConnect
          + '","reason": "Numbers only."}');
      }
      if (!this.store.checks.numberRegEx.test(settings.timeoutAvailability)) {
        result.push(
          '{"setting": "Availability Timout","erroringField": "' + settings.timeoutAvailability
          + '","reason": "Numbers only."}');
      }
      if (!this.store.checks.numberRegEx.test(settings.threadCount)) {
        result.push('{"setting": "Thread Count","erroringField": "' + settings.threadCount
          + '","reason": "Numbers only."}');
      }
      if (!this.store.checks.securityRegEx.test(settings.securitySetting)) {
        result.push('{"setting": "Security Setting","erroringField": "' + settings.securitySetting
          + '","reason": "Must be either ALL, SETTINGS or DEACTIVATE."}');
      }

      return result;
    },
    validateHosts(hosts) {
      let result = [];

      for (let i = 0; i < hosts.length; i++) {
        let hostResult = [];

        if (hosts[i].name.length === 0) {
          hostResult.push('{"setting": "Name","erroringField": "' + hosts[i].name
            + '","reason": "Name is required."}');
        }
        if (hosts[i].address.length === 0 || !this.store.checks.ipRegEx.test(hosts[i].address)
          && !this.store.checks.addressRegEx.test(hosts[i].address)) {
          hostResult.push('{"setting": "Address","erroringField": "' + hosts[i].address
            + '","reason": "Must be IP, hostname or URL"}');
        }
        try {
          if (hosts[i].expectedIp.length > 0 && !this.store.checks.ipRegEx.test(
            hosts[i].expectedIp)) {
            hostResult.push('{"setting": "Expected IP","erroringField": "' + hosts[i].expectedIp
              + '","reason": "Must be a valid IP."}');
          }
        } catch (e) {

        }
        try {
          if (hosts[i].ports.length > 0 && !this.checkPorts(hosts[i].ports)) {
            hostResult.push('{"setting": "Ports","erroringField": "' + hosts[i].ports
              + '","reason": "Ports must be numbers, separated by a comma, only. Each port must not be greater than 65535."}');
          }
        } catch (e) {

        }
        try {
          if (hosts[i].notificationsDisabled.toString.length > 0
            && !this.store.checks.booleanRegEx.test(hosts[i].notificationsDisabled.toString)) {
            hostResult.push('{"setting": "Notifications Disabled","erroringField": "'
              + hosts[i].notificationsDisabled + '","reason": "Must be true or false."}');
          }
        } catch (e) {

        }
        if (hostResult.length > 0) {
          result.push('{"hostIndex": ' + i + ',"errors": [' + hostResult + ']}');
        }

      }

      return result;
    },
    checkPorts(ports) {
      let passed = true;

      if (this.store.checks.portsRegEx.test(ports)) {

        if (ports.includes(",")) {

          for (let number in ports.toString().split(',')) {
            if (parseInt(number) > 65535) {
              passed = false;
            }
          }

        } else if (parseInt(ports) > 65535) {
          passed = false;
        }

      } else {
        passed = false;
      }

      return passed;
    },
    loadSettings() {
      this.$admin.get('/get').then(response => {

        this.store.methods.setSettings(response.data.settings);
        this.store.methods.setHosts(response.data.hosts);

        this.$forceUpdate();

        this.$q.notify({
          position: 'bottom-right',
          message: 'Settings loaded.',
          color: 'info',
          timeout: 1000
        });

      }).catch(error => {
        console.log(error);
        this.$q.notify({
          message: 'Error fetching information.',
          color: 'negative'
        });
      })
    }
  },
  mounted() {
    this.loadSettings();
  }
})
</script>

<style>
.headline {
  font-size: 30px;
}

.sub-headline {
  font-size: 22px;
}

.right {
  position: absolute;
  right: 0;
}

.align-top {
  position: absolute;
  top: 0;
}

.align-bottom {
  position: absolute;
  bottom: 0;
}

.card {
  width: 100%;
  max-width: 400px;
}

/*noinspection CssUnusedSymbol*/
.textarea-dark {
  color: #FFFFFF;
  background-color: #1D1D1D;
}

/*noinspection CssUnusedSymbol*/
.textarea-light {
  color: #1D1D1D;
  background-color: #FFFFFF;
}

/*noinspection CssUnusedSymbol*/
.hosts {
  background-color: black;
}

.monitoring {
  margin: 5px;
  width: 100%;
  max-width: 400px;
  height: 100%;
}
</style>
