import { Activity, CheckCircle2, Clock, PlayCircle, Loader2 } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { jobApi } from './api/client'

export default function App() {
  const { data: jobLogs = [], isLoading, isError } = useQuery({
    queryKey: ['jobLogs'],
    queryFn: jobApi.getLogs,
    refetchInterval: 5000
  });

  const totalJobs = jobLogs.length;
  const completedJobs = jobLogs.filter((job) => job.status === 'COMPLETED').length;
  const inProgressJobs = jobLogs.filter((job) => job.status === 'STARTED' || job.status === 'STARTING').length;
  const failedJobs = jobLogs.filter((job) => job.status === 'FAILED').length;

  const stats = [
    { label: 'Total Executions', value: totalJobs.toString(), icon: Activity, color: 'text-indigo-500', bg: 'bg-indigo-500/10' },
    { label: 'Completed', value: completedJobs.toString(), icon: CheckCircle2, color: 'text-emerald-500', bg: 'bg-emerald-500/10' },
    { label: 'In Progress', value: inProgressJobs.toString(), icon: PlayCircle, color: 'text-blue-500', bg: 'bg-blue-500/10' },
    { label: 'Failed', value: failedJobs.toString(), icon: Clock, color: 'text-rose-500', bg: 'bg-rose-500/10' },
  ]

  const formatTime = (timeStr: string | null) => {
    if (!timeStr) return 'N/A';
    const date = new Date(timeStr);
    return date.toLocaleString();
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Dashboard Overview</h1>
        <div className="text-sm text-slate-500 flex items-center">
          {isLoading && <Loader2 className="w-4 h-4 mr-2 animate-spin" />}
          Last updated: {new Date().toLocaleString()}
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => (
          <div key={stat.label} className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm hover:shadow-md transition-shadow">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-slate-500">{stat.label}</p>
                <p className="text-3xl font-bold text-slate-900 mt-2">{stat.value}</p>
              </div>
              <div className={`w-12 h-12 rounded-lg flex items-center justify-center ${stat.bg}`}>
                <stat.icon className={`w-6 h-6 ${stat.color}`} />
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="px-6 py-5 border-b border-slate-200 flex justify-between items-center bg-slate-50/50">
          <h2 className="text-lg font-semibold text-slate-900">Recent Jobs</h2>
          <button className="text-sm font-medium text-indigo-600 hover:text-indigo-700">View all</button>
        </div>
        <div className="overflow-x-auto">
          {isError ? (
            <div className="p-8 text-center text-rose-500">500 - Internal Server Error</div>
          ) : (
            <table className="w-full text-sm text-left">
              <thead className="text-xs text-slate-500 uppercase bg-slate-50">
                <tr>
                  <th className="px-6 py-4 font-semibold">ID</th>
                  <th className="px-6 py-4 font-semibold">Job Name</th>
                  <th className="px-6 py-4 font-semibold">Status</th>
                  <th className="px-6 py-4 font-semibold">File</th>
                  <th className="px-6 py-4 font-semibold">Start Time</th>
                  <th className="px-6 py-4 font-semibold">Metrics</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {jobLogs.length === 0 && !isLoading && (
                  <tr>
                    <td colSpan={6} className="px-6 py-8 text-center text-slate-500">No job executions found.</td>
                  </tr>
                )}
                {jobLogs.map((job) => (
                  <tr key={job.id} className="hover:bg-slate-50 transition-colors group">
                    <td className="px-6 py-4 font-medium text-slate-900">#{job.id}</td>
                    <td className="px-6 py-4 text-slate-600">{job.jobName}</td>
                    <td className="px-6 py-4">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        job.status === 'COMPLETED' ? 'bg-emerald-100 text-emerald-800' :
                        (job.status === 'STARTED' || job.status === 'STARTING') ? 'bg-blue-100 text-blue-800' :
                        job.status === 'FAILED' ? 'bg-rose-100 text-rose-800' :
                        'bg-slate-100 text-slate-800'
                      }`}>
                        {job.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-slate-500">{job.fileName || 'N/A'}</td>
                    <td className="px-6 py-4 text-slate-500">{formatTime(job.startTime)}</td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2 text-xs">
                        <span className="text-emerald-600 font-medium" title="Success Count">{job.successCount || 0}</span>
                        <span className="text-slate-300">|</span>
                        <span className="text-rose-600 font-medium" title="Failure Count">{job.failureCount || 0}</span>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  )
}
