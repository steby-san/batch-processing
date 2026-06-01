import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
});

export interface JobExecutionLog {
  id: number;
  jobName: string;
  fileName: string | null;
  startTime: string | null;
  endTime: string | null;
  status: string;
  successCount: number | null;
  failureCount: number | null;
  errorMessage: string | null;
}

export const jobApi = {
  getLogs: async (): Promise<JobExecutionLog[]> => {
    const response = await api.get('/jobs/logs');
    return response.data;
  },
  triggerJob: async (): Promise<{ message: string }> => {
    const response = await api.post('/jobs/trigger');
    return response.data;
  },
};

export const reportApi = {
  getFinanceReport: async () => {
    const response = await api.get('/reports/finance');
    return response.data;
  },
  getHrReport: async () => {
    const response = await api.get('/reports/hr');
    return response.data;
  },
};
