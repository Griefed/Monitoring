<template>
  <q-scroll-area class="full-height full-width hosts" ref="scrollArea">
    <div class="hosts">
      <div class="row wrap" style="margin-left: 10px;">
        <q-page-sticky v-if="this.store.hosts.hosts.length > 0" position="left" :offset="[-10, 40]" style="z-index: 1000; margin-top: -80px;">
          <q-btn dense round color="accent" icon="reply" class="rotate-90" @click="scroll('UP')"/>
        </q-page-sticky>
        <q-page-sticky v-if="this.store.hosts.hosts.length > 0" position="left" :offset="[-10, -58]" style="z-index: 1000; margin-bottom: -80px;">
          <q-btn dense round color="accent" icon="reply" class="flipped" @click="scroll('DOWN')"/>
        </q-page-sticky>

        <q-card-section
          style="width: 380px;"
          v-for="(host, index) in this.store.hosts.hosts"
          v-bind:key="host"
        >
          <q-card
            bordered
            style="margin-right: -10px; margin-left: -10px; margin-bottom: -20px;"
            :class=
              "this.$q.dark.isActive ?
                (index % 2 ? 'host-card-dark' : 'host-card-dark-alternative') :
                (index % 2 ? 'host-card' : 'host-card-alternative')"
          >
            <q-badge
              :label="index + 1"
              floating
              style="font-size: 20px;"
              class="q-mr-lg"
              align="top"
            />
            <q-card-section class="text-weight-bolder text-h5 no-wrap host-name roboto-mono-bold">
              {{ host.name.length > 22 ? host.name.substring(0,19) + '...' : host.name }}
            </q-card-section>
            <q-card-section>

              <q-input
                label="Name"
                type="text"
                v-model="host.name"
                :rules="[
                  val => !!val || 'NAME IS REQUIRED!'
                ]"
                label-slot
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
                label="Address"
                type="text"
                v-model="host.address"
                :rules="[
                  val => !val.length === 0 && this.store.checks.ipRegEx.test(val) || this.store.checks.addressRegEx.test(val) || 'MUST BE IP, HOSTNAME, OR URL!'
                ]"
                label-slot
                style="margin-top: -20px;"
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
                label="Ports"
                type="text"
                v-model="host.ports"
                :rules="[
                  val => val === undefined || this.checkPorts(val) || 'NUMBER(S), SEPARATED BY COMMAS ONLY! MUST NOT END WITH A COMMA!'
                ]"
                label-slot
                style="margin-top: -20px;"
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
                label="Expected IP"
                type="text"
                v-model="host.expectedIp"
                :rules="[
                  val => (val === undefined || val.length === 0) || this.store.checks.ipRegEx.test(val) || 'NOT A VALID IP!'
                ]"
                label-slot
                style="margin-top: -20px;"
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
                  label="Notifications Disabled"
                  v-model="host.notificationsDisabled"
                  color="green"
                  checked-icon="checked"
                  indeterminate-icon="notifications"
                  unchecked-icon="clear"
                >
                  <q-tooltip v-if="!this.$q.platform.is.mobile">
                    Whether Notifications should be disabled for this host.
                  </q-tooltip>
                </q-toggle>

                <q-btn
                  color="negative"
                  class="right"
                  label="Delete Host"
                  @click="this.store.hosts.hosts.splice(index,1)"
                />
              </div>
            </q-card-section>
          </q-card>
        </q-card-section>

        <q-card-section style="width: 380px;">
          <q-card bordered :class="this.$q.dark.isActive ? 'host-card-dark' : 'host-card-alternative'" style="height: 328px; margin-right: -10px; margin-left: -10px; margin-bottom: -20px;">
            <q-badge label="n" floating style="font-size: 20px;" class="q-mr-lg" align="top" />
            <q-card-section class="text-weight-bolder text-h4 no-wrap" style="margin-bottom: -40px; margin-top: -10px;">
              Add a new host
            </q-card-section>
            <q-card-section>
              <q-input label="Name" type="text" disable readonly label-slot model-value="">
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Name
                  </div>
                </template>
              </q-input>
              <q-input label="Address" type="text" disable readonly label-slot model-value="">
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Address
                  </div>
                </template>
              </q-input>
              <q-input label="Ports" type="text" disable readonly label-slot model-value="">
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Ports
                  </div>
                </template>
              </q-input>
              <q-input label="Expected IP" type="text" disable readonly label-slot model-value="">
                <template v-slot:label>
                  <div class="row items-center all-pointer-events text-weight-bolder text-h6">
                    Expected IP
                  </div>
                </template>
              </q-input>
              <div class="row no-wrap" style="margin-top: 10px;margin-left: -10px;">
                <q-toggle label="Notifications Disabled" disable color="green" checked-icon="checked" indeterminate-icon="notifications" unchecked-icon="clear" model-value=""/>
                <q-btn color="negative" class="right" label="Delete Host" disable/>
              </div>
              <q-btn icon="add" round dense color="positive" size="50px" style="margin-left: 110px; margin-top: -320px;" @click="addHost"/>
            </q-card-section>
          </q-card>
        </q-card-section>

      </div>
    </div>
  </q-scroll-area>
</template>

<script>
import { defineComponent, inject, ref} from "vue";

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
          scrollArea.value.setScrollPercentage('vertical', 0.0,300)
        } else if (direction === 'DOWN') {
          scrollArea.value.setScrollPercentage('vertical', 1.0,300)
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
