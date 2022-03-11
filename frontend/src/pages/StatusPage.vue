<template>
  <q-page>
    <div class="fullscreen">
      <q-splitter
        v-model="splitterModel"
        style="height: 100%"
        horizontal
        before-class=""
        :after-class="this.$q.dark.isActive ? 'background-dark' : 'background'"
        :limits="[50, 100]"
        separator-style="background: #c0ffee;"
      >

        <!-- HOSTS WHICH ARE UP -->
        <template v-slot:before>
          <q-scroll-area
            class="full-height q-pa-md"
            :thumb-style="downThumbStyle"
            :bar-style="downBarStyle"
          >
            <div
              class="row wrap flex-center"
              v-if="hostsDown.length > 0"
            >
              <q-card
                flat
                class="card-ok"
                :style="'background: ' + getCssColor(host.status, host.hostAvailable) + ';'"
                v-for="host in hostsDown"
                v-bind:key="host"
              >
                <q-list>

                  <q-item-label
                    :class="this.$q.platform.is.mobile ? 'text-weight-bolder text-h6 text-center' : 'text-weight-bolder text-h6 text-center'"
                    :style="this.$q.platform.is.mobile ? 'font-size: 18px;' : 'padding-top: 5px; padding-bottom: 5px;'"
                  >
                    <q-badge
                      v-if="host.code !== 418"
                      class="q-mr-xs"
                      style="font-size: 16px;"
                      floating
                      :label="host.code"
                    />
                    <span>
                      {{ host.name.length > 35 ? host.name.substring(0,32) + '...' : host.name }}
                      <q-tooltip v-if="host.name.length > 35 && !this.$q.platform.is.mobile">
                        {{ host.name }}
                      </q-tooltip>
                    </span>
                    <br>
                    <q-chip
                      outline
                      dense
                      v-ripple
                      clickable
                      :label="host.address.length > 47 ? host.address.substring(0, 44) + '...' : host.address"
                      @click="openURL(host.address)"
                    >
                      <q-tooltip v-if="host.address.length > 47 && !this.$q.platform.is.mobile">
                        {{ host.address }}
                      </q-tooltip>
                    </q-chip>
                  </q-item-label>

                  <q-separator
                    inset
                    size="4px"
                  />

                  <q-item>
                    <q-item-section top>
                      <q-item-label lines="2">
                    <span class="text-weight-bolder right" style="padding-top: 11px; font-size: 17px;">
                      {{ host.status }}
                    </span>
                        <q-chip
                          v-if="host.hostAvailable && host.ip !== 'null' && host.status !== 'DNS MISMATCH'"
                          class="text-weight-bolder"
                          size="lg"
                          dense
                          square
                          flat
                          unelevated
                          color="green"
                          :label="host.ip"
                        />
                        <q-chip
                          v-if="!host.hostAvailable && host.ip !== 'null' && host.status !== 'DNS MISMATCH'"
                          class="text-weight-bolder"
                          size="lg"
                          dense
                          square
                          flat
                          unelevated
                          color="red"
                          :label="host.ip"
                        />
                        <q-chip
                          v-if="host.hostAvailable && host.expectedIp !== 'null' && host.status === 'DNS MISMATCH'"
                          class="text-weight-bolder"
                          size="lg"
                          dense
                          square
                          flat
                          unelevated
                          color="green"
                          :label="host.expectedIp"
                        >
                          <q-tooltip style="font-size: 15px;" v-if="!this.$q.platform.is.mobile">
                            <strong style="color: lawngreen;">Expected:&#160;</strong>{{ host.expectedIp }}<br>
                            <strong style="color: red;">Actual:&#160;&#160;&#160;&#160;&#160;&#160;</strong>{{ host.ip }}
                          </q-tooltip>
                        </q-chip>
                        <q-chip
                          v-if="!host.hostAvailable && host.expectedIp !== 'null' && host.status === 'DNS MISMATCH'"
                          class="text-weight-bolder"
                          size="lg"
                          dense
                          square
                          flat
                          unelevated
                          color="red"
                          :label="host.expectedIp"
                        >
                          <q-tooltip style="font-size: 15px;" v-if="!this.$q.platform.is.mobile">
                            <strong style="color: lawngreen;">Expected:&#160;</strong>{{ host.expectedIp }}<br>
                            <strong style="color: red;">Actual:&#160;&#160;&#160;&#160;&#160;&#160;</strong>{{ host.ip }}
                          </q-tooltip>
                        </q-chip>
                      </q-item-label>
                    </q-item-section>
                  </q-item>

                </q-list>
              </q-card>
            </div>
            <div
              class="absolute-center"
              v-else
            >
              <q-spinner-rings
                :color="this.$q.dark.isActive ? 'primary' : 'secondary'"
                size="10em"
                :thickness="10"
              />
            </div>
          </q-scroll-area>
        </template>

        <template v-slot:separator>
          <q-avatar color="primary" text-color="white" size="40px" icon="drag_indicator"/>
        </template>

        <!-- HOSTS WHICH ARE DOWN -->
        <template v-slot:after>
          <q-scroll-area
            class="full-height q-pa-md"
            :thumb-style="okThumbStyle"
            :bar-style="okBarStyle"
          >
            <div
              v-if="hostsOk.length > 0"
              class="flex-center row wrap"
            >
              <q-chip
                outline
                v-ripple
                v-for="host in hostsOk" v-bind:key="host"
                class="chip-down"
                clickable
                @click="openURL(host.address)"
              >
                <q-item-section>
                  <q-item-label class="text-center text-weight-bolder">
                    <q-badge
                      rounded
                      :color="getQuasarColor(host.status, host.hostAvailable)"
                      floating
                    >
                      {{ host.status }}
                    </q-badge>
                    {{ host.name.length > 25 ? host.name.substring(0,22) + '...' : host.name }}
                  </q-item-label>
                </q-item-section>
                <q-tooltip class="text-center" v-if="host.name.length > 25 && !this.$q.platform.is.mobile">
                  {{ host.name }}<br>
                  {{ host.address }}
                </q-tooltip>
                <q-tooltip v-else-if="!this.$q.platform.is.mobile">
                  {{ host.address }}
                </q-tooltip>
              </q-chip>
            </div>
            <div
              class="absolute-center"
              v-else
            >
              <q-spinner-rings
                :color="this.$q.dark.isActive ? 'primary' : 'secondary'"
                size="5em"
                :thickness="10"
              />
            </div>
          </q-scroll-area>
        </template>

      </q-splitter>
    </div>
  </q-page>
</template>

<script>
import { hosts } from "boot/axios";
import { defineComponent, inject, ref } from 'vue';
import { getCssVar,openURL } from 'quasar';

export default defineComponent({
  name: 'StatusPage',
  setup() {

    const store = inject('store');

    return {
      store,
      splitterModel: ref(90),
      hostsOk: ref([]),
      hostsDown: ref([]),
      ipRegEx: new RegExp('\\d+\\.\\d+\\.\\d+\\.\\d+'),
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
      },
      okThumbStyle: {
        right: '4px',
        borderRadius: '5px',
        backgroundColor: '#325358',
        width: '5px',
        opacity: 0.75
      },

      okBarStyle: {
        right: '2px',
        borderRadius: '9px',
        backgroundColor: '#c0ffee',
        width: '9px',
        opacity: 0.3
      }
    }
  },
  methods: {
    openURL(address) {

      if (this.ipRegEx.test(address)) {
        address = 'http://' + address;
      }

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
    },
    updateHosts() {
      hosts.get().then(response => {

        this.hostsOk = response.data.hostsOk;
        this.hostsDown = response.data.hostsDown;

        this.$forceUpdate();

      }).catch(error => {

        console.log("Encountered an error fetching host information: " + error);

        this.$q.notify({
          message: 'System Error.',
          color: 'negative'
        });
      });
    },
    getCssColor(status, available) {

      if (!available && status !== "OK") {
        // red-10 #ad1e1f
        return '#ad1e1f';
      }

      switch (status) {
// CRITICAL
        case "OFFLINE":

          // red-10 #ad1e1f
          return '#ad1e1f';

        case "DNS MISMATCH":

          // red-10 #ad1e1f
          return '#ad1e1f';

        case "SSL HANDSHAKE ERROR":

          // deep-orange-13 #f13e0e
          return '#f13e0e';

        case "NOT FOUND":

          // orange-6 #f59711
          return '#f59711';

        case "CONNECTION REFUSED":

          // lime-6 #ceda3d
          return '#ceda3d';

// OK / ACCEPTABLE / CONFIG ERROR
        case "UNAVAILABLE":

          // blue-grey #647d8b
          return "#647d8b";

        case "OK":

          return getCssVar('positive');

        case "REDIRECT":

          return getCssVar('info');

        case "INVALID URL":

          // grey-13 #bdbdbd
          return '#bdbdbd';

// EVERYTHING ELSE
        default:
          return getCssVar('primary');
      }
    },
    getQuasarColor(status, available) {

      if (!available && status !== "OK") {
        // red-10 #ad1e1f
        return 'red-10';
      }

      switch (status) {
// CRITICAL
        case "OFFLINE":

          // red-10 #ad1e1f
          return 'red-10';

        case "DNS MISMATCH":

          // red-10 #ad1e1f
          return 'red-10';

        case "SSL HANDSHAKE ERROR":

          // deep-orange-13 #f13e0e
          return 'deep-orange-13';

        case "NOT FOUND":

          // orange-6 #f59711
          return 'orange-6';

        case "CONNECTION REFUSED":

          // lime-6 #ceda3d
          return 'lime-6';

// OK / ACCEPTABLE / CONFIG ERROR
        case "UNAVAILABLE":

          // blue-grey #647d8b
          return "blue-grey";

        case "OK":

          return "positive";

        case "REDIRECT":

          return "info";

        case "INVALID URL":

          // grey-13 #bdbdbd
          return 'grey-13';

// EVERYTHING ELSE
        default:
          return "primary";
      }
    }
  },
  mounted() {
    setInterval(() => {
      this.updateHosts()
    }, parseInt(this.store.state.pollingRate));
  }
})
</script>

<style>
.chip-down {
  margin: 10px;
  width: 100%;
  max-width: 200px;
}

.card-ok {
  margin: 5px;
  width: 100%;
  max-width: 400px;
  height: 120px;
}

/*noinspection CssUnusedSymbol*/
.background {
  height: 100% !important;
  background: bisque !important;
}

/*noinspection CssUnusedSymbol*/
.background-dark {
  height: 100% !important;
  background: #1D1D1D !important;
}

.right {
  position: absolute;
  right: 0;
  padding-right: 10px;
}

/*noinspection CssUnusedSymbol*/
.transparent {
  background-color: transparent !important;
}
</style>
