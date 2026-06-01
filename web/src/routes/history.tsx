import { createFileRoute } from '@tanstack/react-router'
import { useQuery } from '@tanstack/react-query'
import { jobApi } from '../api/client'
import { FileText, Loader2, Search } from 'lucide-react'

export const Route = createFileRoute('/history')({
  component: HistoryPage,
})

function HistoryPage() {
  const { data: jobLogs = [], isLoading, isError } = useQuery({
    queryKey: ['jobLogs'],
    queryFn: jobApi.getLogs,
  });

  const formatTime = (timeStr: string | null) => {
    if (!timeStr) return 'N/A';
    const date = new Date(timeStr);
    return date.toLocaleString();
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Execution History</h1>
          <p className="text-slate-500 mt-1">Full log of all batch processing job executions</p>
        </div>
        <div className="relative w-64">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-4 w-4 text-slate-400" />
          </div>
          <input
            type="text"
            className="block w-full pl-10 pr-3 py-2 border border-slate-200 rounded-lg leading-5 bg-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            placeholder="Filter history..."
          />
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          {isLoading ? (
            <div className="p-12 flex flex-col items-center justify-center text-slate-400">
              <Loader2 className="w-8 h-8 animate-spin mb-4 text-indigo-500" />
              <p>Loading execution history...</p>
            </div>
          ) : isError ? (
            <div className="p-8 text-center text-rose-500">Failed to load history from server.</div>
          ) : (
            <table className="w-full text-sm text-left">
              <thead className="text-xs text-slate-500 uppercase bg-slate-50 border-b border-slate-200">
                <tr>
                  <th className="px-6 py-4 font-semibold">ID</th>
                  <th className="px-6 py-4 font-semibold">Job Name</th>
                  <th className="px-6 py-4 font-semibold">Status</th>
                  <th className="px-6 py-4 font-semibold">Target File</th>
                  <th className="px-6 py-4 font-semibold">Start Time</th>
                  <th className="px-6 py-4 font-semibold">End Time</th>
                  <th className="px-6 py-4 font-semibold">Processed / Failed</th>
                  <th className="px-6 py-4 font-semibold">Error Message</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {jobLogs.length === 0 && (
                  <tr>
                    <td colSpan={8} className="px-6 py-12 text-center text-slate-500">
                      <FileText className="w-12 h-12 mx-auto text-slate-300 mb-3" />
                      <p className="text-lg font-medium text-slate-900">No History Found</p>
                      <p>No job executions have been recorded yet.</p>
                    </td>
                  </tr>
                )}
                {jobLogs.map((job) => (
                  <tr key={job.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="px-6 py-4 font-medium text-slate-900">#{job.id}</td>
                    <td className="px-6 py-4 text-slate-600 font-medium">{job.jobName}</td>
                    <td className="px-6 py-4">
                      <span className={`inline-flex items-center px-2.5 py-1 rounded-md text-xs font-semibold ${
                        job.status === 'COMPLETED' ? 'bg-emerald-100 text-emerald-700' :
                        (job.status === 'STARTED' || job.status === 'STARTING') ? 'bg-blue-100 text-blue-700' :
                        job.status === 'FAILED' ? 'bg-rose-100 text-rose-700' :
                        'bg-slate-100 text-slate-700'
                      }`}>
                        {job.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-slate-500">
                      {job.fileName ? (
                        <span className="bg-slate-100 text-slate-700 px-2 py-1 rounded text-xs font-mono">
                          {job.fileName}
                        </span>
                      ) : 'N/A'}
                    </td>
                    <td className="px-6 py-4 text-slate-500 whitespace-nowrap">{formatTime(job.startTime)}</td>
                    <td className="px-6 py-4 text-slate-500 whitespace-nowrap">{formatTime(job.endTime)}</td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2 font-mono text-sm">
                        <span className="text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded">{job.successCount || 0}</span>
                        <span className="text-rose-600 bg-rose-50 px-2 py-0.5 rounded">{job.failureCount || 0}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-slate-500 max-w-xs truncate" title={job.errorMessage || ''}>
                      {job.errorMessage || '-'}
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
