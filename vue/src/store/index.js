import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import issuesServiceModule from './issuesServiceModule'

Vue.use(Vuex)

/*
 * The authorization header is set for axios when you login but what happens when you come back or
 * the page is refreshed. When that happens you need to check for the token in local storage and if it
 * exists you should set the header so that it will be attached to each request
 */
const currentToken = localStorage.getItem('token')
const currentUser = JSON.parse(localStorage.getItem('user'));

// new shit
const currentIssue = localStorage.getItem('issue');
const currentChoice = localStorage.getItem('option');

if(currentToken != null) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${currentToken}`;
}

export default new Vuex.Store({
  modules: {
    IssuesService: issuesServiceModule
  },
  state: {
    latest: null,
    token: currentToken || '',
    user: currentUser || {},

    // new shit
    issue: currentIssue || {
      issueId: "",
      issueName: "",
      description: "",
      dateExpiration: "",
      timeExpiration: "",
      genreTag: "",
    },
    option: currentChoice || {}
  },
  mutations: {
    SET_AUTH_TOKEN(state, token) {
      state.token = token;
      localStorage.setItem('token', token);
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    },
    SET_USER(state, user) {
      state.user = user;
      localStorage.setItem('user',JSON.stringify(user));
    },
    LOGOUT(state) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      state.token = '';
      state.user = {};
      axios.defaults.headers.common = {};
    },

    // new shit
    ADD_ISSUE(state, issue) {
      state.issue = issue;
      localStorage.setItem('issue', issue);
    },
    ADD_CHOICE(state, option) {
      state.option = option;
      localStorage.setItem('option', option);
    },
    GET_LATEST_ID(state, id) {
      state.latest = id;
    }
  },
  // actions: {
  //   getTheId({commit}){
  //     IssuesService.mostRecentIssueId().then(id =>{
  //       commit("GET_LATEST_ID", id)
  //     })
  //   }
  // },
})
