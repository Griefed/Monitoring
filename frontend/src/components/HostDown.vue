<template>
  <q-card
    :class="this.store.state.blink ? (getCssColor(host.status) === '#ad1e1f' ? 'card-ok blink' : 'card-ok') : 'card-ok'"
    :style="'background: ' + getCssColor(host.status) + ';'"
    bordered
  >
    <q-list>

      <q-item-label
        :class="this.$q.platform.is.mobile ? 'text-weight-bolder text-h6 text-center' : 'text-weight-bolder text-h6 text-center'"
        :style="this.$q.platform.is.mobile ? 'font-size: 18px;' : 'padding-top: 5px; padding-bottom: 5px;'"
      >
        <q-badge
          v-if="host.code !== 418"
          :label="host.code"
          class="q-mr-xs"
          floating
          style="font-size: 16px;"
        />
        <span>
        {{ host.name.length > 35 ? host.name.substring(0, 32) + '...' : host.name }}
        <q-tooltip v-if="host.name.length > 35 && !this.$q.platform.is.mobile">
          {{ host.name }}
        </q-tooltip>
      </span>
        <br>
        <q-chip
          v-ripple
          :label="host.address.length > 47 ? host.address.substring(0, 44) + '...' : host.address"
          clickable
          dense
          outline
          @click="this.store.methods.openInTab(host.address)"
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
                    <span class="text-weight-bolder right"
                          style="padding-top: 11px; font-size: 17px;">
                      {{ host.status }}
                    </span>
            <q-chip
              v-if="host.hostAvailable && host.ip !== 'null' && host.status !== 'DNS MISMATCH'"
              :label="host.ip"
              class="text-weight-bolder"
              color="green"
              dense
              flat
              size="lg"
              square
              unelevated
            />
            <q-chip
              v-if="!host.hostAvailable && host.ip !== 'null' && host.status !== 'DNS MISMATCH'"
              :label="host.ip"
              class="text-weight-bolder"
              color="red"
              dense
              flat
              size="lg"
              square
              unelevated
            />
            <q-chip
              v-if="host.hostAvailable && host.expectedIp !== 'null' && host.status === 'DNS MISMATCH'"
              :label="host.expectedIp"
              class="text-weight-bolder"
              color="green"
              dense
              flat
              size="lg"
              square
              unelevated
            >
              <q-tooltip v-if="!this.$q.platform.is.mobile" style="font-size: 15px;">
                <strong style="color: lawngreen;">Expected:&#160;</strong>{{ host.expectedIp }}<br>
                <strong style="color: red;">Actual:&#160;&#160;&#160;&#160;&#160;&#160;</strong>{{
                  host.ip
                }}
              </q-tooltip>
            </q-chip>
            <q-chip
              v-if="!host.hostAvailable && host.expectedIp !== 'null' && host.status === 'DNS MISMATCH'"
              :label="host.expectedIp"
              class="text-weight-bolder"
              color="red"
              dense
              flat
              size="lg"
              square
              unelevated
            >
              <q-tooltip v-if="!this.$q.platform.is.mobile" style="font-size: 15px;">
                <strong style="color: lawngreen;">Expected:&#160;</strong>{{ host.expectedIp }}<br>
                <strong style="color: red;">Actual:&#160;&#160;&#160;&#160;&#160;&#160;</strong>{{
                  host.ip
                }}
              </q-tooltip>
            </q-chip>
          </q-item-label>
        </q-item-section>
      </q-item>

    </q-list>
  </q-card>
</template>

<script>
import {defineComponent, inject} from 'vue';
import {getCssVar} from 'quasar';

export default defineComponent({
  name: 'HostDown',
  props: {
    host: {
      type: Object,
      typeValue: String
    }
  },
  setup() {

    const store = inject('store');

    return {
      store,
    }
  },
  methods: {
    getCssColor(status) {

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
    }
  }
})
</script>

<style>
@keyframes blinking {
  0% {
    background-color: #7e1011;
  }
  100% {
    background-color: #ad1e1f;
  }
}

/*noinspection CssUnusedSymbol*/
.blink {
  width: 200px;
  height: 200px;
  animation: blinking 1s infinite;
}

/*noinspection CssUnusedSymbol*/
.card-ok {
  margin: 5px;
  width: 100%;
  max-width: 400px;
  height: 120px;
}

.right {
  position: absolute;
  right: 0;
  padding-right: 10px;
}
</style>
