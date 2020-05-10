import http from 'k6/http';
import {check, sleep} from 'k6';

export let options = {
  stages: [
    {duration: '10s', target: 100},
    {duration: '20s', target: 100},
    {duration: '10s', target: 0},
  ],
};

export default () => {
	check(http.batch([
		'http://localhost/demo/one',
		'http://localhost/demo/two'
	]), {
		'First service status is OK': r => r[0].status === 200,
		'First service duration less than 200ms': r => r[0].timings.duration < 200,
		'Second service status is OK': r => r[1].status === 200,
		'Second service duration less than 200ms': r => r[1].timings.duration < 200
	})
  sleep(0.3);
}

