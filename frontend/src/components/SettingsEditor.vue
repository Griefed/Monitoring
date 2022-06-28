<template>
  <div class="row flex-center text-weight-bolder text-h5">
    Settings
  </div>
  <q-scroll-area style="height: 500px; width: 100%;">

    <q-input
      v-model="this.store.settings.defaultPorts"
      :rules="[
                  val => this.checkPorts(val) || 'NUMBER(S), SEPARATED BY COMMAS ONLY! MUST NOT END WITH A COMMA!'
                ]"
      label="Default Ports"
    >
      <q-tooltip :disable="this.$q.platform.is.mobile" anchor="bottom right">
        Default ports with which to check for host availability if a regular ping failed.
      </q-tooltip>
    </q-input>

    <div class="row no-wrap">
      <q-toggle
        v-model="this.store.settings.additivePorts"
        :label="this.store.settings.additivePorts ? 'Additive Ports Enabled' : 'Additive Ports Disabled'"
        checked-icon="checked"
        color="green"
        indeterminate-icon="add_link"
        style="width: 50%;"
        unchecked-icon="clear"
      >
        <q-tooltip :disable="this.$q.platform.is.mobile" anchor="bottom right">
          Whether default ports should be added to host-configured ports.
        </q-tooltip>
      </q-toggle>

      <q-toggle
        v-model="this.store.settings.notificationsEnabled"
        :label="this.store.settings.notificationsEnabled ? 'Notifications Enabled' : 'Notifications Disabled'"
        checked-icon="checked"
        color="green"
        indeterminate-icon="notifications"
        style="width: 50%;"
        unchecked-icon="clear"
      >
        <q-tooltip :disable="this.$q.platform.is.mobile" anchor="bottom right">
          Whether notifications should be globally enabled or disabled.
        </q-tooltip>
      </q-toggle>
    </div>

    <q-input
      v-model.number="this.store.settings.particlesCount"
      :rules="[
                  val => this.store.checks.numberRegEx.test(val) || 'NUMBER(S) ONLY!'
                ]"
      label="Particles Amount"
    >
      <q-tooltip :disable="this.$q.platform.is.mobile" anchor="bottom right">
        The amount of particles to display in the webfrontend.
      </q-tooltip>
    </q-input>

    <q-input
      v-model.number="this.store.settings.pollingRate"
      :rules="[
                  val => this.store.checks.numberRegEx.test(val) || 'NUMBER(S) ONLY!'
                ]"
      label="Polling Rate"
    >
      <q-tooltip :disable="this.$q.platform.is.mobile" anchor="bottom right">
        The rate in milliseconds at which the webfrontend should refresh.
      </q-tooltip>
    </q-input>

    <q-input
      v-model.number="this.store.settings.timeoutConnect"
      :rules="[
                  val => this.store.checks.numberRegEx.test(val) || 'NUMBER(S) ONLY!'
                ]"
      label="Connection Timeout"
    >
      <q-tooltip :disable="this.$q.platform.is.mobile" anchor="bottom right">
        Seconds to wait until a connection timeout is triggered for getting information from hosts.
      </q-tooltip>
    </q-input>

    <q-input
      v-model.number="this.store.settings.timeoutAvailability"
      :rules="[
                  val => this.store.checks.numberRegEx.test(val) || 'NUMBER(S) ONLY!'
                ]"
      label="Availability Timeout"
    >
      <q-tooltip :disable="this.$q.platform.is.mobile" anchor="bottom right">
        Seconds to wait until a read timeout is triggered for getting information from hosts.
      </q-tooltip>
    </q-input>

    <q-input
      v-model.number="this.store.settings.threadCount"
      :rules="[
                  val => this.store.checks.numberRegEx.test(val) || 'NUMBER(S) ONLY!'
                ]"
      label="Thread Count"
    >
      <q-tooltip :disable="this.$q.platform.is.mobile" anchor="bottom right">
        Number of threads used for updating host information.<br>
        Changing this at runtime requires a restart in order for changes to take effect.
      </q-tooltip>
    </q-input>

    <q-input
      v-model="this.store.settings.securitySetting"
      :rules="[
                  val => this.store.checks.securityRegEx.test(val) || 'EITHER ALL, SETTINGS OR DEACTIVATE!'
                ]"
      label="Security Setting"
    >
      <q-tooltip :disable="this.$q.platform.is.mobile" anchor="bottom right">
        Security setting. Either ALL,SETTINGS or DEACTIVATE.<br>
        Changing this at runtime requires a restart in order for changes to take effect.
      </q-tooltip>
    </q-input>
  </q-scroll-area>
</template>

<script>
import {defineComponent, inject} from "vue";

export default defineComponent({
  name: "SettingsEditor",
  setup() {
    const store = inject('store');
    return {
      store
    }
  },
  methods: {
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
    }
  }
})
</script>

<style scoped>
</style>
