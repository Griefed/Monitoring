<template>
  <q-page>
    <div class="fullscreen">
      <q-splitter
        v-model="splitterModel"
        :after-class="this.$q.dark.isActive ? 'background-dark' : 'background'"
        :limits="[50, 100]"
        before-class=""
        horizontal
        separator-style="background: #c0ffee;"
        style="height: 100%"
      >

        <!-- HOSTS WHICH ARE UP -->
        <template v-slot:before>
          <q-scroll-area
            :bar-style="downBarStyle"
            :thumb-style="downThumbStyle"
            class="full-height q-pa-xs"
          >
            <div
              v-if="hostsDown.length > 0"
              class="row wrap flex-center"
            >
              <HostDown
                v-for="host in hostsDown"
                v-bind:key="host"
                :host="host"
              />
            </div>
            <div
              v-else
              class="absolute-center"
            >
              <q-spinner-rings
                :color="this.$q.dark.isActive ? 'primary' : 'secondary'"
                :thickness="10"
                size="10em"
              />
            </div>
          </q-scroll-area>
        </template>

        <template v-slot:separator>
          <q-avatar color="primary" icon="drag_indicator" size="40px" text-color="white"/>
        </template>

        <!-- HOSTS WHICH ARE DOWN -->
        <template v-slot:after>
          <q-scroll-area
            :bar-style="okBarStyle"
            :thumb-style="okThumbStyle"
            class="full-height"
          >
            <div
              v-if="hostsOk.length > 0"
              class="flex-center row wrap"
            >
              <HostOk
                v-for="host in hostsOk"
                v-bind:key="host"
                :host="host"
              />
            </div>
            <div
              v-else
              class="absolute-center"
            >
              <q-spinner-rings
                :color="this.$q.dark.isActive ? 'primary' : 'secondary'"
                :thickness="10"
                size="5em"
              />
            </div>
          </q-scroll-area>
        </template>

      </q-splitter>
    </div>
  </q-page>
</template>

<script>
import {hosts} from "boot/axios";
import {defineComponent, inject, ref} from 'vue';
import HostDown from "components/HostDown";
import HostOk from "components/HostOk";

export default defineComponent({
  name: 'StatusPage',
  components: {HostOk, HostDown},
  setup() {

    const store = inject('store');

    return {
      store,
      splitterModel: ref(90),
      hostsOk: ref([]),
      hostsDown: ref([]),
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
    updateHosts() {
      hosts.get().then(response => {

        this.hostsOk = response.data.hostsOk;
        this.hostsDown = response.data.hostsDown;

        this.$forceUpdate();

      }).catch(error => {

        this.hostsOk = [];
        this.hostsDown = [];

        this.$forceUpdate();

        console.log("Error fetching host information: " + error);

        this.$q.notify({
          message: 'Error fetching host information.',
          color: 'negative'
        });
      });
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
</style>
