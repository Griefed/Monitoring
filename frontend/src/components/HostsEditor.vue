<template>
  <q-scroll-area ref="scrollArea" class="full-height full-width hosts">
    <div class="hosts">
      <div class="row wrap" style="margin-left: 10px;">
        <q-page-sticky v-if="this.store.hosts.hosts.length > 0" :offset="[-10, 40]" position="left"
                       style="z-index: 1000; margin-top: -80px;">
          <q-btn class="rotate-90" color="accent" dense icon="reply" round @click="scroll('UP')"/>
        </q-page-sticky>
        <q-page-sticky v-if="this.store.hosts.hosts.length > 0" :offset="[-10, -58]" position="left"
                       style="z-index: 1000; margin-bottom: -80px;">
          <q-btn class="flipped" color="accent" dense icon="reply" round @click="scroll('DOWN')"/>
        </q-page-sticky>

        <q-card-section
          v-for="(host, index) in this.store.hosts.hosts"
          v-bind:key="host"
          style="width: 380px;"
        >
          <q-card
            :class=
              "this.$q.dark.isActive ?
                (index % 2 ? 'host-card-dark' : 'host-card-dark-alternative') :
                (index % 2 ? 'host-card' : 'host-card-alternative')"
            bordered
            style="margin-right: -10px; margin-left: -10px; margin-bottom: -20px;"
          >
            <q-badge
              :label="index + 1"
              align="top"
              class="q-mr-lg"
              floating
              style="font-size: 20px;"
            />
            <q-card-section class="text-weight-bolder text-h5 no-wrap host-name roboto-mono-bold">
              {{ host.name.length > 22 ? host.name.substring(0, 19) + '...' : host.name }}
            </q-card-section>
            <q-card-section>

              <q-input
                v-model="host.name"
                :rules="[
                  val => !!val || 'NAME IS REQUIRED!'
                ]"
                label="Name"
                label-slot
                type="text"
              >
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Name
                  </div>
                </template>
                <q-tooltip v-if="!this.$q.platform.is.mobile">
                  The display name of this host.
                </q-tooltip>
              </q-input>

              <q-input
                v-model="host.address"
                :rules="[
                  val => !val.length === 0 && this.store.checks.ipRegEx.test(val) || this.store.checks.addressRegEx.test(val) || 'MUST BE IP, HOSTNAME, OR URL!'
                ]"
                label="Address"
                label-slot
                style="margin-top: -20px;"
                type="text"
              >
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Address
                  </div>
                </template>
                <q-tooltip v-if="!this.$q.platform.is.mobile">
                  The address of this host. URL, IP or FQDN.
                </q-tooltip>
              </q-input>

              <q-input
                v-model="host.ports"
                :rules="[
                  val => val === undefined || this.checkPorts(val) || 'NUMBER(S), SEPARATED BY COMMAS ONLY! MUST NOT END WITH A COMMA!'
                ]"
                label="Ports"
                label-slot
                style="margin-top: -20px;"
                type="text"
              >
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Ports
                  </div>
                </template>
                <q-tooltip v-if="!this.$q.platform.is.mobile">
                  Comma-separated list of ports to check the host with.
                </q-tooltip>
              </q-input>

              <q-input
                v-model="host.expectedIp"
                :rules="[
                  val => (val === undefined || val.length === 0) || this.store.checks.ipRegEx.test(val) || 'NOT A VALID IP!'
                ]"
                label="Expected IP"
                label-slot
                style="margin-top: -20px;"
                type="text"
              >
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Expected IP
                  </div>
                </template>
                <q-tooltip v-if="!this.$q.platform.is.mobile">
                  The IP to compare the automatically acquired IP against.
                </q-tooltip>
              </q-input>

              <div class="row no-wrap" style="margin-top: -10px; margin-left: -10px;">
                <q-toggle
                  v-model="host.notificationsDisabled"
                  checked-icon="checked"
                  color="green"
                  indeterminate-icon="notifications"
                  label="Notifications Disabled"
                  unchecked-icon="clear"
                >
                  <q-tooltip v-if="!this.$q.platform.is.mobile">
                    Whether Notifications should be disabled for this host.
                  </q-tooltip>
                </q-toggle>

                <q-btn
                  class="right"
                  color="negative"
                  label="Delete Host"
                  @click="this.store.hosts.hosts.splice(index,1)"
                />
              </div>
            </q-card-section>
          </q-card>
        </q-card-section>

        <q-card-section style="width: 380px;">
          <q-card :class="this.$q.dark.isActive ? 'host-card-dark' : 'host-card-alternative'"
                  bordered
                  style="height: 328px; margin-right: -10px; margin-left: -10px; margin-bottom: -20px;">
            <q-badge align="top" class="q-mr-lg" floating label="n" style="font-size: 20px;"/>
            <q-card-section class="text-weight-bolder text-h4 no-wrap"
                            style="margin-bottom: -40px; margin-top: -10px;">
              Add a new host
            </q-card-section>
            <q-card-section>
              <q-input disable label="Name" label-slot model-value="" readonly type="text">
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Name
                  </div>
                </template>
              </q-input>
              <q-input disable label="Address" label-slot model-value="" readonly type="text">
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Address
                  </div>
                </template>
              </q-input>
              <q-input disable label="Ports" label-slot model-value="" readonly type="text">
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Ports
                  </div>
                </template>
              </q-input>
              <q-input disable label="Expected IP" label-slot model-value="" readonly type="text">
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Expected IP
                  </div>
                </template>
              </q-input>
              <div class="row no-wrap" style="margin-top: 10px;margin-left: -10px;">
                <q-toggle checked-icon="checked" color="green" disable
                          indeterminate-icon="notifications" label="Notifications Disabled"
                          model-value="" unchecked-icon="clear"/>
                <q-btn class="right" color="negative" disable label="Delete Host"/>
              </div>
              <q-btn color="positive" dense icon="add" round size="50px"
                     style="margin-left: 110px; margin-top: -320px;" @click="addHost"/>
            </q-card-section>
          </q-card>
        </q-card-section>

      </div>
    </div>
  </q-scroll-area>
</template>

<script>
import {defineComponent, inject, ref} from "vue";

export default defineComponent({
  name: "HostsEditor",
  setup() {

    const store = inject('store');

    const scrollArea = ref(null);

    return {
      store,
      scrollArea,
      scroll(direction) {
        if (direction === 'UP') {
          scrollArea.value.setScrollPercentage('vertical', 0.0, 300)
        } else if (direction === 'DOWN') {
          scrollArea.value.setScrollPercentage('vertical', 1.0, 300)
        }
      }
    }
  },
  methods: {
    checkPorts(ports) {
      if (ports === undefined || ports.length === 0) {
        return true;
      }

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
    addHost() {
      this.store.hosts.hosts.push(JSON.parse(
        "{\"name\": \"\",\"address\": \"\"}"
      ));
    }
  },
  mounted() {

  }
})
</script>

<style scoped>
.host-name {
  margin-bottom: -40px;
  margin-top: -10px;
  margin-left: -10px;
  height: 70px;
  width: 100%;
}

.right {
  position: absolute;
  right: 0;
  margin-right: 15px;
}

.flipped {
  -o-transform: scaleX(-1) rotate(270deg);
  -moz-transform: scaleX(-1) rotate(270deg);
  -webkit-transform: scaleX(-1) rotate(270deg);
  -ms-transform: scaleX(-1) rotate(270deg);
  transform: scaleX(-1) rotate(270deg);
}

/*noinspection CssUnusedSymbol*/
.host-card {
  background-color: whitesmoke;
}

/*noinspection CssUnusedSymbol*/
.host-card-alternative {
  background-color: white;
}

/*noinspection CssUnusedSymbol*/
.host-card-dark {
  background-color: #101010;
}

/*noinspection CssUnusedSymbol*/
.host-card-dark-alternative {
  background-color: #1D1D1D;
}
</style>
