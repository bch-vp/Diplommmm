<template>
  <div>
    <v-app-bar absolute flat fixed dark color="transparent" style="box-shadow: 0 0 50px black; padding-top: 1em ">
      <v-toolbar-title class="font-weight-medium light-green--text text--accent-2"
                       style="margin-left: 2%; font-size: 40px; font-family: 'Monoton', cursive; ">
        JEWELRY
      </v-toolbar-title>

      <v-btn v-on:click="$store.commit('show_home')" :disabled="$store.state.App.isHome" text rounded small outlined fab
             style="margin-left: 4%; margin-right: 1em ">
        <v-icon>home</v-icon>
      </v-btn>
      |
      <v-col cols="2">
        <v-select
            style="padding-left: 1em"
            v-model="selectCategory"
            v-bind:hint=text_page.form_component.button.choose_category
            :items="$store.state.App.categories"
            item-text="name"
            :disabled="!$store.state.App.isHome"
            persistent-hint
            return-object
            single-line
        ></v-select>
      </v-col>
      <v-col cols="3">
        <v-text-field
            :disabled="!$store.state.App.isHome"
            v-model="$store.state.App.search"
            v-bind:label=text_page.form_component.button.search
            outlined
            dense
        ></v-text-field>
      </v-col>

      <v-spacer></v-spacer>

      <v-btn v-on:click="$store.commit('show_profile')" :disabled="$store.state.App.isProfile" rounded text>
        <img v-if="$store.state.Profile.isAvatarExists" :src="$store.state.Profile.avatarUrl" class="avatar"/>
        <v-icon v-if="!$store.state.Profile.isAvatarExists">
          perm_identity
        </v-icon>
        &nbsp
        {{ text_page.header.role }}
      </v-btn>

      <v-btn rounded text href="do?command=sign_out" style="padding-left: 2em">
        <v-icon>exit_to_app</v-icon>
      </v-btn>
    </v-app-bar>
  </div>
</template>

<script>
export default {
  data() {
    return {
      text_page: text_page,
      isAvatarExists: undefined,

      selectCategory: undefined
    }
  },
  watch: {
    selectCategory() {
      this.$store.state.App.search = ''
      this.$store.commit('set_selectCategory', this.selectCategory)
      this.axios({
        method: 'post',
        url: '/ajax?command=load_all_products_by_category',
        data: this.selectCategory
      }).then(response => {
            var array = response.data.data.sort((a, b) => (a.id < b.id) ? 1 : -1)
            this.$store.commit('set_products', array)
          },
          ex => {
            if (ex.response.status === 500) {
              window.location.href = '/jsp/error500.jsp'
            }
            if (ex.response.status === 403) {
              window.location.href = '/jsp/error403.jsp'
            }
          })
    }
  },
  created() {
    this.axios({
      method: 'get',
      url: '/ajax?command=load_all_orders',
    }).then(resp => {
      var array = resp.data.sort((a, b) => (a.status < b.status) ? 1 : -1)
      array.forEach(order => {
        order.dateCreatedAt = new Date(order.dateCreatedAt).toLocaleDateString()
      })

      var arrayResult = []
      var arrayConcat = array.filter(function (order) {
        return order.status === 'NOT_CONFIRMED';
      })
      arrayConcat = arrayConcat.sort((a, b) => (a.id < b.id) ? 1 : -1)
      arrayResult = arrayResult.concat(arrayConcat)

      arrayConcat = array.filter(function (order) {
        return order.status === 'CONFIRMED';
      })
      arrayConcat = arrayConcat.sort((a, b) => (a.id < b.id) ? 1 : -1)
      arrayResult = arrayResult.concat(arrayConcat)


      this.$store.commit('set_userOrders', arrayResult)
    }, ex => {
      if (ex.response.status === 500) {
        window.location.href = '/jsp/error500.jsp'
      }
      if (ex.response.status === 403) {
        window.location.href = '/jsp/error403.jsp'
      }
    })

    this.axios({
      method: 'get',
      url: '/ajax?command=load_profile_image',
    }).then(resp => {
      this.$store.commit('set_isAvatarExists', true)
      this.$store.commit('change_avatarUrl', resp.data.url)
    }, ex => {
      this.$store.commit('set_isAvatarExists', false)
      this.$store.commit('change_avatarUrl', '')
    })

    this.axios({
      method: 'get',
      url: '/ajax?command=load_all_categories'
    }).then(response => {
          var array = response.data.data.sort((a, b) => (a.id > b.id) ? 1 : -1)
          this.$store.commit('set_categories', array)
          this.selectCategory = response.data.data[0]
        },
        ex => {
          if (ex.response.status === 500) {
            window.location.href = '/jsp/error500.jsp'
          }
          if (ex.response.status === 403) {
            window.location.href = '/jsp/error403.jsp'
          }
        })

    this.axios({
      method: 'get',
      url: '/ajax?command=load_all_clients'
    }).then(response => {
          var users = response.data.users.sort(function (a, b) {
            return a.id - b.id
          })
          this.$store.commit('set_users', response.data.users)
        },
        ex => {
          if (ex.response.status === 500) {
            window.location.href = '/jsp/error500.jsp'
          }
          if (ex.response.status === 403) {
            window.location.href = '/jsp/error403.jsp'
          }
        })
  },
  methods: {}
}
</script>

<style scoped>
.list-item {
  justify-content: center;
}

.avatar {
  vertical-align: middle;
  width: 50px;
  height: 50px;
  border-radius: 50%;
}
</style>