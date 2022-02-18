import { boot } from 'quasar/wrappers';
import axios from 'axios';

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)

const hosts = axios.create(
  {
    headers: {
      'Content-Type': 'application/json'
    },
    baseURL: '/api/v1/hosts'
  }
);

const api = axios.create(
  {
    headers: {
      'Content-Type': 'application/json'
    },
    baseURL: '/api/v1'
  }
);

export default boot(({ app }) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$agents = hosts
  // ^ ^ ^ this will allow you to use this.$agents (for Vue Options API form)
  //       so you can easily perform requests against your app's API

  app.config.globalProperties.$api = api
  // ^ ^ ^ this will allow you to use this.$agents (for Vue Options API form)
  //       so you can easily perform requests against your app's API
});

export { hosts, api }
