import { Bell, Search, Loader2 } from 'lucide-react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { jobApi } from '../api/client'

export default function Header() {
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
    <header className="h-16 border-b border-slate-200 bg-white/80 backdrop-blur-md sticky top-0 z-10 flex items-center justify-between px-8 shadow-sm">
      <div className="flex items-center flex-1">
        <div className="relative w-full max-w-md">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-4 w-4 text-slate-400" />
          </div>
          <input
            type="text"
            className="block w-full pl-10 pr-3 py-2 border border-slate-200 rounded-lg leading-5 bg-slate-50 placeholder-slate-400 focus:outline-none focus:bg-white focus:ring-2 focus:ring-indigo-500/50 focus:border-indigo-500 sm:text-sm transition-all duration-200"
            placeholder="Search jobs, tasks, or settings..."
          />
        </div>
      </div>
      
      <div className="flex items-center space-x-4">
        <button className="relative p-2 text-slate-400 hover:text-slate-500 hover:bg-slate-100 rounded-full transition-colors">
          <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-rose-500 border-2 border-white"></span>
          <Bell className="h-5 w-5" />
        </button>
        <button 
          onClick={() => triggerMutation.mutate()}
          disabled={triggerMutation.isPending}
          className="flex items-center px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 shadow-sm shadow-indigo-600/20 transition-all hover:shadow-indigo-600/40 active:scale-95 disabled:opacity-70 disabled:cursor-not-allowed"
        >
          {triggerMutation.isPending ? <Loader2 className="w-4 h-4 mr-2 animate-spin" /> : <span className="mr-2">+</span>}
          {triggerMutation.isPending ? 'Starting...' : 'Trigger Job'}
        </button>
      </div>
    </header>
  )
}
