<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>

        <q-icon name="img:favicon.gif" style="font-size: 32px;" />

        <q-toolbar-title>
          <b>Monitoring</b>
          <span style="margin-left: 10px;">
            v{{this.store.state.version}}
          </span>
          <q-btn
            v-if="this.store.state.updateAvailable"
            style="margin-left: 5px; margin-bottom: 4px;"
            round
            dense
            size="xs"
            color="warning"
            icon="priority_high"
            @click="showUpdateNotification()"
          />
        </q-toolbar-title>

        <q-btn v-if="!this.$q.platform.is.mobile" label="GitHub" style="color: #C0FFEE" type="a" target="_blank" href="https://github.com/SuK-IT/Monitoring">
          <q-tooltip>
            Visit the project on GitHub!
          </q-tooltip>
        </q-btn>

        <q-separator style="margin-left: 5px; margin-right: 5px;"/>

        <q-btn v-if="!this.$q.platform.is.mobile" label="Support" style="color: #C0FFEE" type="a" target="_blank" href="https://github.com/SuK-IT/Monitoring/issues">
          <q-tooltip>
            Report an issue!
          </q-tooltip>
        </q-btn>

        <q-separator
          style="margin-left: 15px; margin-right: 15px;"
          color="secondary"
          inset
          vertical
        />

        <q-btn-dropdown
          icon="settings"
          >
          <q-list>
            <q-item class="flex-center">
              <q-btn
                :icon="this.$q.dark.isActive ? 'nights_stay' : 'wb_sunny'"
                :style="this.$q.dark.isActive ? 'color: #FFFFFF;' : 'color: #000000;'"
                label="Toggle Dark Mode"
                @click="toggleDarkMode()"
                dense
                flat
                v-close-popup
              >
                <q-tooltip :disable="this.$q.platform.is.mobile">
                  {{ this.$q.dark.isActive ? 'Deactivate Dark Mode' : 'Activate Dark Mode' }}
                </q-tooltip>
              </q-btn>
            </q-item>

            <q-item class="flex-center">
              <q-btn
                icon="model_training"
                :style="this.$q.dark.isActive ? 'color: #FFFFFF;' : 'color: #000000;'"
                label="Toggle Blinking"
                @click="toggleBlinking()"
                dense
                flat
                v-close-popup
              >
                <q-tooltip :disable="this.$q.platform.is.mobile">
                  {{ this.store.state.blink ? 'Deactivate Blinking Of Critical Cards' : 'Activate Blinking Of Critical Cards' }}
                </q-tooltip>
              </q-btn>
            </q-item>

            <q-item class="flex-center">
              <q-btn
                v-if="!this.$q.platform.is.mobile"
                :icon="this.$q.fullscreen.isActive ? 'fullscreen_exit' : 'fullscreen'"
                :style="this.$q.dark.isActive ? 'color: #FFFFFF;' : 'color: #000000;'"
                label="Toggle Fullscreen"
                @click="this.$q.fullscreen.toggle()"
                dense
                flat
                v-close-popup
              >
                <q-tooltip :disable="this.$q.platform.is.mobile">
                  {{ this.$q.fullscreen.isActive ? 'Exit Fullscreen' : 'Toggle Fullscreen' }}
                </q-tooltip>
              </q-btn>
            </q-item>
          </q-list>
        </q-btn-dropdown>

      </q-toolbar>
    </q-header>

    <q-page-container>
      <q-page class="row no-wrap">
        <div class="col">
          <div class="full-height full-width">
            <q-scroll-area class="full-height full-width page">
              <div
                id="particles-js"
                :class="this.$q.dark.isActive ? 'dark_gradient' : 'normal_gradient'"
              ><router-view/></div>

            </q-scroll-area>
          </div>
        </div>
      </q-page>
    </q-page-container>

  </q-layout>
</template>

<script>
import { defineComponent, ref, inject } from 'vue';
import { tsParticles } from 'tsparticles';
import { openURL } from 'quasar';

export default defineComponent({
  name: 'MainLayout',
  data() {

    const store = inject('store');

    return {
      store,
      updateNotification: ref(false),
      drawer: ref(true),
      miniState: ref(true)
    }
  },
  methods : {
    toggleBlinking() {
      this.store.state.blink = !this.store.state.blink;
      this.$q.cookies.set('blink.isActive', this.store.state.blink);
    },
    toggleDarkMode() {
      this.$q.dark.toggle();
      this.$q.cookies.set('dark.isActive', this.$q.dark.isActive);
    },
    noReminder() {
      this.store.state.updateReminder = false;
      this.$q.cookies.set('update.remind', this.store.state.updateReminder);
    },
    showUpdateNotification() {
      this.$q.notify({
        color: 'primary',
        textColor: 'secondary',
        position: 'center',
        multiline: true,
        html: true,
        timeout: 10000,
        progress: true,
        actions: [
          { label: 'VISIT RELEASE PAGE', color: 'positive',
            handler: () => {
              openURL(
                this.store.state.updateLink,
                null,
                {
                  noopener: true,
                  menubar: false,
                  toolbar: false,
                  noreferrer: false,
                }
              );
            }
          },
          { label: 'OK', color: 'info' },
          { label: 'DO NOT REMIND ME AGAIN', color: 'warning',
            handler: () => {
            this.noReminder();
            }
          }
        ],
        message:
          '<div class="text-h4 text-weight-bolder text-center">Update available!</div><br><br>' +
          '<span class="text-h6 text-weight-bolder absolute-center">Version ' + this.store.state.updateVersion + '</span><br>' +
          '<span class=\"text-h6\">Available at: <br>' + this.store.state.updateLink + '</span><br>'
      })
    }
  },
  mounted() {
    if (this.$q.cookies.has('dark.isActive')) {
      this.$q.dark.set(this.$q.cookies.get('dark.isActive'));
    } else {
      this.$q.cookies.set('dark.isActive', this.$q.dark.isActive);
    }

    if (this.$q.cookies.has('update.remind')) {
      this.store.state.updateReminder = this.$q.cookies.get('update.remind');
    } else {
      this.store.state.updateReminder = true;
      this.$q.cookies.set('update.remind', true);
    }

    if (this.$q.cookies.has('blink.isActive')) {
      this.store.state.blink = this.$q.cookies.get('blink.isActive');
    } else {
      this.store.state.blink = true;
      this.$q.cookies.set('blink.isActive', this.store.state.blink);
    }

    this.$settings.get().then(response => {
      this.store.state.version = response.data.version;
      this.store.state.pollingRate = response.data.pollingRate;
      this.store.state.particlesCount = response.data.particlesCount;
      tsParticles.load("particles-js",{
        "fpsLimit": 30,
        "particles": {
          "number": {
            "value": this.store.state.particlesCount,
            "density": {
              "enable": true,
              "value_area": 800
            }
          },
          "color": {
            "value": ["#325358","#C0FFEE","#31CCEC","#6A1A78"]
          },
          "shape": {
            "type": ["circle","triangle","edge","polygon"],
            "stroke": {
              "width": 0,
              "color": ["#325358","#C0FFEE","#31CCEC","#6A1A78"]
            },
            "polygon": {
              "nb_sides": 6
            }
          },
          "opacity": {
            "value": 1,
            "random": true,
            "anim": {
              "enable": true,
              "speed": 1,
              "opacity_min": 0.1,
              "sync": false
            }
          },
          "size": {
            "value": 3.5,
            "random": true,
            "anim": {
              "enable": true,
              "speed": 1,
              "size_min": 0.1,
              "sync": false
            }
          },
          "links": {
            "enable": true,
            "distance": 150,
            "color": "#C0FFEE",
            "opacity": 0.4,
            "width": 1
          },
          "move": {
            "enable": true,
            "speed": 1.5,
            "direction": "right",
            "random": true,
            "straight": false,
            "outModes": {
              "default": "out",
              "bottom": "out",
              "left": "out",
              "right": "out",
              "top": "out"
            },
            "bounce": false
          },
        },
        "interactivity": {
          "detect_on": "canvas",
          "events": {
            "onhover": {
              "enable": true,
              "mode": ["bubble","grab"]
            },
            "onclick": {
              "enable": true,
              "mode": "push"
            },
            "resize": true
          },
          "modes": {
            "grab": {
              "distance": 140,
              "line_linked": {
                "opacity": 1
              }
            },
            "bubble": {
              "distance": 200,
              "size": 4,
              "duration": 5,
              "opacity": 1,
              "speed": 0.1
            },
            "push": {
              "particles_nb": 4
            }
          }
        },
        "retina_detect": true
      });
    }).catch(error => {
      console.log(error);
      this.$q.notify({
        message: 'System Error.',
        color: 'negative'
      });
    });

    this.$api.get('/updates').then(response => {
      this.store.state.updateAvailable = response.data.available;
      this.store.state.updateVersion = response.data.version;
      this.store.state.updateLink = response.data.link;
      if (response.data.available && this.store.state.updateReminder) {
        this.showUpdateNotification();
      }
    }).catch(error => {
      console.log(error);
      this.$q.notify({
        message: 'System Error.',
        color: 'negative'
      });
    })
  }
})
</script>

<style>
#particles-js {
  position: absolute;
  width: 100%;
  height: 100%;
  background-repeat: no-repeat;
  background-size: cover;
  background-position: 50% 50%;
}

/*noinspection CssUnusedSymbol*/
.normal_gradient {
  background:
    radial-gradient(circle at 0% 0%,
    rgba(161, 232, 213, 0.4),
    rgba(50, 83, 88, 0.1),
    rgba(50, 83, 88, 0.05) 100%
    ),
    radial-gradient(circle at 100% 0%,
    rgba(133, 213, 212, 0.4),
    rgba(50, 83, 88, 0.1),
    rgba(50, 83, 88, 0.1) 100%
    ),
    radial-gradient(circle at 0% 100%,
    rgba(197, 142, 248, 0.4),
    rgba(50, 83, 88, 0.1),
    rgba(50, 83, 88, 0.05) 100%
    ),
    radial-gradient(circle at 100% 100%,
    rgba(171, 115, 224, 0.56),
    rgba(50, 83, 88, 0.1),
    rgba(50, 83, 88, 0.05) 100%
    ),
    radial-gradient(circle at 50% 50%,
    rgba(143, 147, 196, 0.4),
    rgba(50, 83, 88, 0.1),
    rgba(50, 83, 88, 0.05) 100%
    ),
    radial-gradient(circle at 25% 50%,
    rgba(96, 168, 151, 0.9),
    rgba(50, 83, 88, 0),
    rgba(50, 83, 88, 0) 100%
    ),
    radial-gradient(circle at 75% 50%,
    rgba(107, 67, 190, 0.9),
    rgba(50, 83, 88, 0),
    rgba(50, 83, 88, 0) 100%
    ),
    radial-gradient(circle at 50% 25%,
    rgba(97, 166, 176, 0.9),
    rgba(50, 83, 88, 0),
    rgba(50, 83, 88, 0) 100%
    ),
    radial-gradient(circle at 50% 75%,
    rgba(137, 200, 210, 0.9),
    rgba(50, 83, 88, 0),
    rgba(50, 83, 88, 0) 100%
    );
}

/*noinspection CssUnusedSymbol*/
.dark_gradient {
  background:
    radial-gradient(circle at 0% 0%,
    rgba(34, 81, 114, 0.6),
    rgba(50, 83, 88, 0.1),
    rgba(50, 83, 88, 0.05) 100%
    ),
    radial-gradient(circle at 100% 0%,
    rgba(17, 87, 85, 0.4),
    rgba(50, 83, 88, 0.1),
    rgba(50, 83, 88, 0.1) 100%
    ),
    radial-gradient(circle at 0% 100%,
    rgb(49, 26, 133),
    rgba(50, 83, 88, 0.1),
    rgba(50, 83, 88, 0.05) 100%
    ),
    radial-gradient(circle at 100% 100%,
    rgba(80, 20, 136, 0.6),
    rgba(50, 83, 88, 0.1),
    rgba(50, 83, 88, 0.05) 100%
    ),
    radial-gradient(circle at 50% 50%,
    rgba(128, 134, 204, 0.4),
    rgba(50, 83, 88, 0.1),
    rgba(50, 83, 88, 0.05) 100%
    ),
    radial-gradient(circle at 25% 50%,
    rgba(66, 117, 105, 0.9),
    rgba(50, 83, 88, 0),
    rgba(50, 83, 88, 0) 100%
    ),
    radial-gradient(circle at 75% 50%,
    rgba(98, 69, 157, 0.9),
    rgba(50, 83, 88, 0),
    rgba(50, 83, 88, 0) 100%
    ),
    radial-gradient(circle at 50% 25%,
    rgba(26, 79, 87, 0.9),
    rgba(50, 83, 88, 0),
    rgba(50, 83, 88, 0) 100%
    ),
    radial-gradient(circle at 50% 75%,
    rgba(18, 35, 89, 1),
    rgba(50, 83, 88, 0),
    rgba(50, 83, 88, 0) 100%
    );
}

/*noinspection CssUnknownTarget*/
.page {
  background-image: url("~assets/background.webp");
  background-repeat: repeat;
  background-attachment: fixed;
}

a:link {
  text-decoration: none;
}

a:visited {
  text-decoration: none;
}

a:hover {
  text-decoration: none;
}

a:active {
  text-decoration: none;
}
</style>
