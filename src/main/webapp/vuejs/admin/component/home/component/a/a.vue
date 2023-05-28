<style>
.apexcharts-menu-item {
  color: black;
}
.apexcharts-tooltip-series-group.apexcharts-active, .apexcharts-tooltip-series-group:last-child {
  color: black;
}
.apexcharts-tooltip.apexcharts-theme-light .apexcharts-tooltip-title {
 color: black;
}

</style>
<template>
  <div class="profile-wrapper">
    <v-container>
      <v-row justify="center" row>
        <v-col sm="9" md="9" lg="8" xl="5">
          <v-select label="Select" v-model="selectedProduct"
                    :items="productsWithFilters"></v-select>
          <h3>Amount needed for next week: {{this.sumNeed}}</h3>
          <v-btn @click="submit(10)" color="black" class="red" v-if="sumNeed==0 && sumAvailable==0">
            Need a new supply of 10 units!
          </v-btn>
          <v-btn @click="submit(sumNeed + Math.round(sumNeed/3))" color="black" class="red" v-else-if="sumAvailable<=(this.sumNeed+this.sumNeed/3)">
            Need a new supply of
            <span>&nbsp{{this.sumNeed + Math.round(this.sumNeed/3)}}&nbsp</span>
             units!
          </v-btn>
          <br><br>
          <h3>
            Sales forecast chart for next week:
          </h3>
          <apexchart width="700" align="center" type="area" :options="options" :series="series"></apexchart>
          <br>
            <h3>Sales chart for the last 4 weeks:</h3>
            <apexchart width="700" type="area"  :options="chartOptions" :series="seriesa"></apexchart>
        </v-col>
      </v-row>
    </v-container>
  </div>



</template>

<script>

import VueApexCharts from 'vue-apexcharts'
export default {
  components: {
    VueApexCharts
  },
  data() {
    return {
      selectedProduct: '',

      text_page: text_page,
      spinnerVisible: false,
      isError: false,
      purchaseData:null,
      sumNeed:0,
      sumAvailable:0,
      options: {
        chart: {
          id: 'vuechart-example'
        },
        xaxis: {
          type: 'day',
          categories: ["1 Day", "2 Day", "3 Day", "4 Day", "5 Day", "6 Day", "7 Day"]
        }
      },
      series: [],
      seriesa: [],
      chartOptions: {
        chart: {
          height: 350,
          type: 'area'
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          curve: 'smooth'
        },
        xaxis: {
          type: 'day',
          categories: ["1 Day", "2 Day", "3 Day", "4 Day", "5 Day", "6 Day", "7 Day"]
        },
        tooltip: {
          x: {
            format: 'dddd'
          },
        },
      },
      products: this.$store.state.App.products,

    }
  },
  computed: {
    productsWithFilters() {

      var list = this.$store.state.App.products.map(function(item) {
        return 'Id : ' + item['id'] + ' |  Name : ' + item['name'] + ' |  Available : ' + item['count'];
      })
      this.selectedProduct = list[0];
      return list
    }
  },
  watch: {
    selectedProduct: function (newValue) {
      this.updateChart()
    },
  },
  methods: {
      submit(a) {
        var product = this.$store.state.App.products.find(obj => obj.id === parseInt(this.id));
        this.sumNeed = this.sumNeed
          this.axios({
            method: 'post',
            url: '/ajax?command=update_product_info',
            data: {
              id: String(product.id),
              name: product.name,
              info: product.info,
              price: product.price,
              count: parseInt(this.sumAvailable) + a,
            }
          }).then(response => {
                product.count = a + parseInt(this.sumAvailable)
                this.sumAvailable = a + parseInt(this.sumAvailable)
            this.axios({
              method: 'post',
              url: '/ajax?command=update_product_status',
              data: {
                id_status: String(1),
                id_product: String(this.id)
              }
            }).then(response => {
                  product.status='ACTIVE'
                },
                ex => {
                  if (ex.response.status === 500) {
                    window.location.href = '/jsp/error500.jsp'
                  }
                  if (ex.response.status === 403) {
                    window.location.href = '/jsp/error403.jsp'
                  }

                  this.isError = true
                  this.spinnerVisible = false
                })

              },
              ex => {
                if (ex.response.status === 500) {
                  window.location.href = '/jsp/error500.jsp'
                }
                if (ex.response.status === 403) {
                  window.location.href = '/jsp/error403.jsp'
                }
                if (ex.response.status === 400) {
                  this.error = text_page.form_component.error.name_not_unique
                } else{
                  this.error = text_page.form_component.error.notification
                }

                this.reset()
                this.isSuccess = false
                this.isError = true

                this.spinnerVisible = false

                this.await1Seconds()
              })
      },
    updateColor(color) {
      alert(color)

    },
    updateChart(){
      this.seriesa=[]
      this.series=[]
      this.sumNeed=0
      this.sumAvailable = this.selectedProduct.substring(this.selectedProduct.lastIndexOf("Available") + 11)
      this.id = this.selectedProduct.substring(5, this.selectedProduct.lastIndexOf("Name")-4)

      this.axios({
        method: 'post',
        url: '/ajax?command=load_purchase_data',
        data: {
          id: this.id
        }
      }).then(resp => {
        var firstWeek = {
          name: '1 week',
          data: resp.data.firstWeek.map(function(item) {
            return item['count'];
          })
        };
        this.seriesa.push(firstWeek);

        var secondWeek = {
          name: '2 week',
          data: resp.data.secondWeek.map(function(item) {
            return item['count'];
          })
        };
        this.seriesa.push(secondWeek);

        var thirdWeek = {
          name: '3 week',
          data: resp.data.thirdWeek.map(function(item) {
            return item['count'];
          })
        };
        this.seriesa.push(thirdWeek);

        var fourthWeek = {
          name: '4 week',
          data: resp.data.fourthWeek.map(function(item) {
            return item['count'];
          })
        };
        this.seriesa.push(fourthWeek);
        var resultWeek = {
          name: 'next week',
          data: resp.data.resultWeek.map(function(item) {
            return item['count'];
          })
        };
        this.series.push(resultWeek);
        resultWeek.data.forEach(item => {
          this.sumNeed += parseInt(item);
        });


      }, ex => {
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

  },
}
</script>

<style scoped>

</style>