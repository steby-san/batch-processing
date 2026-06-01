import { Link, useLocation } from '@tanstack/react-router'
import { LayoutDashboard, List, History } from 'lucide-react'

const navItems = [
  { icon: LayoutDashboard, label: 'Dashboard', to: '/' },
  { icon: List, label: 'Jobs', to: '/jobs' },
  { icon: History, label: 'History', to: '/history' },
]

export default function Sidebar() {
  const location = useLocation()
  
  return (
    <aside className="w-64 h-screen bg-slate-900 border-r border-slate-800 flex flex-col transition-all duration-300">
      <div className="h-16 flex items-center px-6 border-b border-slate-800">
        <span className="text-white font-bold text-lg tracking-wide">Test</span>
      </div>
      
      <div className="flex-1 py-6 px-3">
        <nav className="space-y-1">
          {navItems.map((item) => {
            const Icon = item.icon
            const isActive = location.pathname === item.to
            return (
              <Link
                key={item.label}
                to={item.to}
                className={`flex items-center px-3 py-2.5 rounded-lg transition-all duration-200 group ${
                  isActive 
                    ? 'bg-indigo-500/10 text-indigo-400' 
                    : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200'
                }`}
              >
                <Icon 
                  className={`w-5 h-5 mr-3 transition-colors ${
                    isActive ? 'text-indigo-400' : 'text-slate-500 group-hover:text-slate-300'
                  }`} 
                />
                <span className="font-medium text-sm">{item.label}</span>
              </Link>
            )
          })}
        </nav>
      </div>
      
    </aside>
  )
}
