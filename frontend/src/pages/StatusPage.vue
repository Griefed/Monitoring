<template>
  <q-page>
    <!--<img class="flex absolute-center" alt="SUK-IT logo" src="~assets/logo.webp">-->

    <!-- HOSTS WHICH ARE UP -->
    <div class="row flex-center transparent" style="padding-bottom: 120px;">
      <q-card flat :style="'margin: 5px; width: 100%; max-width: 400px; height: 120px; background: ' + getCssColor(host.status, host.hostAvailable) + ';'" v-for="host in hostsDown" v-bind:key="host">
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
                  v-if="host.hostAvailable && host.ip !== 'null'"
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
                  v-if="!host.hostAvailable && host.ip !== 'null'"
                  class="text-weight-bolder"
                  size="lg"
                  dense
                  square
                  flat
                  unelevated
                  color="red"
                  :label="host.ip"
                />
              </q-item-label>
            </q-item-section>
          </q-item>

        </q-list>
      </q-card>
    </div>

    <!-- HOSTS WHICH ARE DOWN -->
    <q-card class="fixed-bottom vertical-bottom window-width" style="width: 100%">
      <q-scroll-area class="vertical-bottom window-width" style="height: 120px; width: 100%">
        <q-card-section class="row wrap flex-center">

          <q-chip
            outline
            v-ripple
            v-for="host in hostsOk" v-bind:key="host"
            style="margin: 10px; width: 100%; max-width: 200px;"
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

        </q-card-section>
      </q-scroll-area>
    </q-card>

  </q-page>

</template>

<script>
import { hosts,api } from "boot/axios";
import { defineComponent, inject, ref } from 'vue';
import { getCssVar,openURL } from 'quasar';

export default defineComponent({
  name: 'StatusPage',
  setup() {

    const store = inject('store');

    let hostsOk = ref(null);
    let hostsDown = ref(null);

    let ipRegEx = new RegExp('\\d+\\.\\d+\\.\\d+\\.\\d+')

    return {
      store,
      hostsOk,
      hostsDown,
      polling: 5000,
      ipRegEx
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

        let hosts = response.data.hosts;
        let ok = [];
        let down = [];

        hosts.forEach(host => {
          if (host.code === 200 || host.code === 301) {
            ok.push(host);
          } else {
            down.push(host);
          }
        })

        this.hostsOk = ok;
        this.hostsDown = down;

        this.$forceUpdate();

      }).catch(error => {

        console.log("Encountered an error fetching host information: " + error);

      });
    },
    getCssColor(status, available) {

      if (!available && status !== "OK") {
        // red-10 #ad1e1f
        return '#ad1e1f';
      }

      switch (status) {
// CRITICAL
        case "UNAVAILABLE":

          // blue-grey #647d8b
          return "#647d8b";

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
        case "UNAVAILABLE":

          // blue-grey #647d8b
          return "blue-grey";

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
    api.get('/polling').then(response => {

      this.polling = response.data.polling;

      setInterval(() => {
        this.updateHosts()
      }, parseInt(this.polling));

    }).catch(error => {

      console.log("Couldn't fetch polling rate: " + error);

    });
  }
})
</script>

<style>
.right {
  position: absolute;
  right: 0;
  padding-right: 10px;
}

.transparent {
  background-color: transparent;
}
</style>
