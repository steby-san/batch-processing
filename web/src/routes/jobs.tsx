import { createFileRoute } from '@tanstack/react-router'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { jobApi } from '../api/client'
import { PlayCircle, FileCode2, Loader2, ArrowRight } from 'lucide-react'

export const Route = createFileRoute('/jobs')({
  component: JobsPage,
})

function JobsPage() {
  const queryClient = useQueryClient();
  
  const triggerMutation = useMutation({
    mutationFn: jobApi.triggerJob,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['jobLogs'] });
      alert('Job triggered successfully!');
    },
    onError: () => {
      alert('Failed to trigger job');
    }
  });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Available Jobs</h1>
        <p className="text-slate-500 mt-1">Manage and trigger batch processing jobs manually</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

        <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden flex flex-col transition-shadow hover:shadow-md">
          <div className="p-6 flex-1">
            <div className="w-12 h-12 rounded-lg bg-indigo-500/10 flex items-center justify-center mb-4 border border-indigo-500/20">
              <FileCode2 className="w-6 h-6 text-indigo-600" />
            </div>
            <h3 className="text-lg font-bold text-slate-900 mb-2">CSV Data Import Job</h3>
            <p className="text-slate-500 text-sm mb-4">
              Reads the configured CSV file, processes records via Spring Batch, and inserts them into the database. Skips malformed records automatically.
            </p>
            
            <div className="space-y-2 mb-6">
              <div className="flex justify-between text-sm">
                <span className="text-slate-500">Chunk Size:</span>
                <span className="font-mono font-medium text-slate-700">10</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-slate-500">Target Table:</span>
                <span className="font-mono font-medium text-slate-700">PERSON</span>
              </div>
            </div>
          </div>
          <div className="p-4 border-t border-slate-100 bg-slate-50/50">
            <button 
              onClick={() => triggerMutation.mutate()}
              disabled={triggerMutation.isPending}
              className="w-full flex items-center justify-center px-4 py-2.5 bg-white border border-slate-300 rounded-lg text-sm font-semibold text-slate-700 hover:bg-slate-50 hover:text-indigo-600 hover:border-indigo-300 transition-all disabled:opacity-50 disabled:cursor-not-allowed group"
            >
              {triggerMutation.isPending ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin text-indigo-500" />
                  Starting Job...
                </>
              ) : (
                <>
                  <PlayCircle className="w-4 h-4 mr-2 text-slate-400 group-hover:text-indigo-500 transition-colors" />
                  Run Job Manually
                  <ArrowRight className="w-4 h-4 ml-2 opacity-0 -translate-x-2 group-hover:opacity-100 group-hover:translate-x-0 transition-all duration-300" />
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
