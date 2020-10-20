import Vue from 'vue'
import {
  Button, Container, Main, Row, Footer, Card, FormItem, Form, Input, Message, MessageBox, Aside, MenuItemGroup,
  Menu, MenuItem,Submenu
} from 'element-ui'

Vue.prototype.$message = Message
Vue.prototype.$confirm = MessageBox.confirm

Vue.use(Button)
Vue.use(Container)
Vue.use(Main)
Vue.use(Row)
Vue.use(Footer)
Vue.use(Card)
Vue.use(FormItem)
Vue.use(Form)
Vue.use(Input)
Vue.use(Aside)
Vue.use(Menu)
Vue.use(MenuItem)
Vue.use(MenuItemGroup)
Vue.use(Submenu)
