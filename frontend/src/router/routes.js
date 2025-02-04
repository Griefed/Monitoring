
const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/StatusPage.vue') }
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '', component: () => import('pages/StatusPage.vue')
      }
    ]
  }
]

export default routes
