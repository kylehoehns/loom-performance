import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    discardResponseBodies: true,
    scenarios: {
        contacts: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '10s', target: 1000 },
                { duration: '30s', target: 1000 },
            ],
            gracefulRampDown: '10s',
        },
    },
};
export default function () {
    http.get('http://localhost:8080/test');
    sleep(1);
}
