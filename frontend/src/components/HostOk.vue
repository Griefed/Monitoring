<template>
  <q-chip
    outline
    v-ripple
    class="chip-down"
    clickable
    @click="this.store.methods.openInTab(host.address)"
  >
    <q-item-section>
      <q-item-label class="text-center text-weight-bolder">
        <q-badge
          rounded
          :color="getQuasarColor(host.status)"
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
</template>

<script>
import { defineComponent, inject } from 'vue';
import { openURL } from 'quasar';

export default defineComponent({
  name: 'HostOk',
  props: {
    host: {
      type: Object,
      typeValue: String
    }
  },
  setup() {

    const store = inject('store');

    return {
      store
    }
  },
  methods: {
    getQuasarColor(status) {

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
  }
})
</script>

<style>
.chip-down {
  margin: 10px;
  width: 100%;
  max-width: 200px;
}
</style>
