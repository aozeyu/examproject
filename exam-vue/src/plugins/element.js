import Vue from 'vue'
import {
  Button, Container, Main, Row, Footer, Card, FormItem, Form, Input, Message, MessageBox, Aside, MenuItemGroup,
  Menu, MenuItem,Submenu,Col,Header,Tooltip,Breadcrumb,BreadcrumbItem,Dropdown,DropdownItem,DropdownMenu,Notification,
  Tag
} from 'element-ui'

Vue.prototype.$message = Message
Vue.prototype.$notify = Notification
Vue.prototype.$confirm = MessageBox.confirm

Vue.use(Button)
Vue.use(Tag)
Vue.use(DropdownMenu)
Vue.use(DropdownItem)
Vue.use(Dropdown)
Vue.use(Container)
Vue.use(BreadcrumbItem)
Vue.use(Tooltip)
Vue.use(Main)
Vue.use(Row)
Vue.use(Footer)
Vue.use(Card)
Vue.use(FormItem)
Vue.use(Form)
Vue.use(Input)
Vue.use(Aside)
Vue.use(Menu)
Vue.use(Col)
Vue.use(MenuItem)
Vue.use(MenuItemGroup)
Vue.use(Header)
Vue.use(Submenu)
Vue.use(Breadcrumb)
