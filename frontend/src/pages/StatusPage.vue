<template>
  <q-page>
    <!--<img class="flex absolute-center" alt="SUK-IT logo" src="~assets/logo.webp">-->

    <!-- HOSTS WHICH ARE UP -->
    <div class="row flex-center transparent" style="padding-bottom: 120px;">
      <q-card flat style="margin: 5px; width: 100%; max-width: 400px;" v-for="host in hostsDown" v-bind:key="host">
        <q-list>

          <q-item-label
            :class="this.$q.platform.is.mobile ? 'text-weight-bolder text-h6 text-center' : 'text-weight-bolder text-h6 text-center'"
            :style="this.$q.platform.is.mobile ? 'font-size: 18px;' : 'padding-top: 5px; padding-bottom: 5px;'"
          >
            {{host.name}}
            <q-badge
              v-if="host.code != 418"
              class="q-mr-xs right"
              text-color="white"
              rounded
              align="bottom"
              color="red"
              floating
            >
              {{ host.code }}
            </q-badge>
          </q-item-label>

          <q-separator/>

          <q-item>
            <q-item-section top>
              <q-item-label lines="3">
                <span class="text-weight-bolder text-red-8 right">{{ host.status }}</span>
                <span class="text-weight-bolder">{{host.address}}</span><br>
                <q-chip
                  v-if="host.hostAvailable && host.ip !== 'null'"
                  class="text-weight-bolder"
                  dense
                  color="green"
                  :label="host.ip"
                />
                <q-chip
                  v-if="!host.hostAvailable && host.ip !== 'null'"
                  class="text-weight-bolder"
                  dense
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

          <q-chip outline v-ripple v-for="host in hostsOk" v-bind:key="host" style="margin: 10px; width: 100%; max-width: 200px;">
            <q-item-section>
              <q-item-label class="text-center text-weight-bolder">
                <q-badge rounded color="green" floating>{{ host.status }}</q-badge>
                {{ host.name }}
              </q-item-label>
            </q-item-section>
            <q-tooltip v-if="!this.$q.platform.is.mobile">
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
import { defineComponent, inject } from 'vue';
import { ref } from 'vue';

export default defineComponent({
  name: 'StatusPage',
  setup() {

    const store = inject('store');

    let hostsOk = ref(null);
    let hostsDown = ref(null);

    return {
      store,
      hostsOk,
      hostsDown,
      polling: 5000
    }
  },
  methods: {
    updateHosts() {
      hosts.get().then(response => {

        let hosts = response.data.hosts;
        let ok = [];
        let down = [];

        hosts.forEach(host => {
          if (host.code === 200) {
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

    refreshValues() {
      this.updateHosts()
    }

  },
  mounted() {
    api.get('/polling').then(response => {

      this.polling = response.data.polling;

      setInterval(() => {
        this.refreshValues()
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
