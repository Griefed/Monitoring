<template>
      <div class="row flex-center text-weight-bolder text-h5">
        Settings
      </div>
      <q-scroll-area style="height: 500px; width: 100%;">

        <q-input
          v-model="this.store.settings.defaultPorts"
          label="Default Ports"
          :rules="[
                  val => this.checkPorts(val) || 'NUMBER(S), SEPARATED BY COMMAS ONLY! MUST NOT END WITH A COMMA!'
                ]"
        >
          <q-tooltip anchor="bottom right" :disable="this.$q.platform.is.mobile">
            Default ports with which to check for host availability if a regular ping failed.
          </q-tooltip>
        </q-input>

        <div class="row no-wrap">
          <q-toggle
            style="width: 50%;"
            :label="this.store.settings.additivePorts ? 'Additive Ports Enabled' : 'Additive Ports Disabled'"
            v-model="this.store.settings.additivePorts"
            color="green"
            checked-icon="checked"
            indeterminate-icon="add_link"
            unchecked-icon="clear"
          >
            <q-tooltip anchor="bottom right" :disable="this.$q.platform.is.mobile">
              Whether default ports should be added to host-configured ports.
            </q-tooltip>
          </q-toggle>

          <q-toggle
            style="width: 50%;"
            :label="this.store.settings.notificationsEnabled ? 'Notifications Enabled' : 'Notifications Disabled'"
            v-model="this.store.settings.notificationsEnabled"
            color="green"
            checked-icon="checked"
            indeterminate-icon="notifications"
            unchecked-icon="clear"
          >
            <q-tooltip anchor="bottom right" :disable="this.$q.platform.is.mobile">
              Whether notifications should be globally enabled or disabled.
            </q-tooltip>
          </q-toggle>
        </div>

        <q-input
          v-model.number="this.store.settings.particlesCount"
          label="Particles Amount"
          :rules="[
                  val => this.store.checks.numberRegEx.test(val) || 'NUMBER(S) ONLY!'
                ]"
        >
          <q-tooltip anchor="bottom right" :disable="this.$q.platform.is.mobile">
            The amount of particles to display in the webfrontend.
          </q-tooltip>
        </q-input>

        <q-input
          v-model.number="this.store.settings.pollingRate"
          label="Polling Rate"
          :rules="[
                  val => this.store.checks.numberRegEx.test(val) || 'NUMBER(S) ONLY!'
                ]"
        >
          <q-tooltip anchor="bottom right" :disable="this.$q.platform.is.mobile">
            The rate in milliseconds at which the webfrontend should refresh.
          </q-tooltip>
        </q-input>

        <q-input
          v-model.number="this.store.settings.timeoutConnect"
          label="Connection Timeout"
          :rules="[
                  val => this.store.checks.numberRegEx.test(val) || 'NUMBER(S) ONLY!'
                ]"
        >
          <q-tooltip anchor="bottom right" :disable="this.$q.platform.is.mobile">
            Seconds to wait until a connection timeout is triggered for getting information from hosts.
          </q-tooltip>
        </q-input>

        <q-input
          v-model.number="this.store.settings.timeoutAvailability"
          label="Availability Timeout"
          :rules="[
                  val => this.store.checks.numberRegEx.test(val) || 'NUMBER(S) ONLY!'
                ]"
        >
          <q-tooltip anchor="bottom right" :disable="this.$q.platform.is.mobile">
            Seconds to wait until a read timeout is triggered for getting information from hosts.
          </q-tooltip>
        </q-input>

        <q-input
          v-model.number="this.store.settings.threadCount"
          label="Thread Count"
          :rules="[
                  val => this.store.checks.numberRegEx.test(val) || 'NUMBER(S) ONLY!'
                ]"
        >
          <q-tooltip anchor="bottom right" :disable="this.$q.platform.is.mobile">
            Number of threads used for updating host information.<br>
            Changing this at runtime requires a restart in order for changes to take effect.
          </q-tooltip>
        </q-input>

        <q-input
          v-model="this.store.settings.securitySetting"
          label="Security Setting"
          :rules="[
                  val => this.store.checks.securityRegEx.test(val) || 'EITHER ALL, SETTINGS OR DEACTIVATE!'
                ]"
        >
          <q-tooltip anchor="bottom right" :disable="this.$q.platform.is.mobile">
            Security setting. Either ALL,SETTINGS or DEACTIVATE.<br>
            Changing this at runtime requires a restart in order for changes to take effect.
          </q-tooltip>
        </q-input>
      </q-scroll-area>
</template>

<script>
import { defineComponent, inject } from "vue";

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
