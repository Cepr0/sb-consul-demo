import http from 'k6/http';
import {check, sleep} from 'k6';

export let options = {
	vus: 1
};

export default () => {
	check(http.batch([
		'http://localhost/demo/one',
		'http://localhost/demo/two'
	]), {
		'First service status is OK': r => r[0].status === 200,
		'Second service status is OK': r => r[1].status === 200
	})
  sleep(1);
}

