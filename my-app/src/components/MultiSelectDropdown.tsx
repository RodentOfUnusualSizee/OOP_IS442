import React, { useState } from 'react';

interface Option {
    label: string;
    value: string;
}

interface MultiSelectDropdownProps {
    options: Option[];
    selectedOptions: Option[];
    onSelectedOptionsChange: (selectedOptions: Option[]) => void;
    key: string;
}

const MultiSelectDropdown = ({ options, selectedOptions, onSelectedOptionsChange, key }: MultiSelectDropdownProps) => {
    const [isOpen, setIsOpen] = useState(false);

    const toggleOption = (option: Option) => {
        const isSelected = selectedOptions.some((selectedOption) => selectedOption.value === option.value);

        if (isSelected) {
            onSelectedOptionsChange(selectedOptions.filter((selectedOption) => selectedOption.value !== option.value));
        } else {
            onSelectedOptionsChange([...selectedOptions, option]);
        }
    };

    return (
        <div className="flex-1 my-6 px-6">
            <div>
                <button
                    type="button"
                    className="relative w-full bg-white border border-gray-300 rounded-md shadow-sm pl-3 pr-10 py-2 text-left cursor-default focus:outline-none focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                    onClick={() => setIsOpen(!isOpen)}
                >
                    <span className="flex items-center">
                        <span className="ml-3 block truncate">
                            {selectedOptions.length === 0 ? 'Select options' : selectedOptions.map((option) => option.label).join(', ')}
                        </span>
                    </span>
                    <span className="ml-3 absolute inset-y-0 right-0 flex items-center pr-2 pointer-events-none">
                        <svg className="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                            <path fillRule="evenodd" d="M6.293 7.293a1 1 0 011.414 0L10 9.586l2.293-2.293a1 1 0 011.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" clipRule="evenodd" />
                        </svg>
                    </span>
                </button>
            </div>
            <div className={`absolute z-10 mt-1 w-full bg-white shadow-lg ${isOpen ? '' : 'hidden'}`}>
                <ul className="max-h-60 overflow-auto">
                    {options.map((option) => (
                        <li key={`${key}-${option.value}`} className="relative">
                            <button
                                type="button"
                                className={`w-full text-left py-2 px-3 ${selectedOptions.some((selectedOption) => selectedOption.value === option.value)
                                    ? 'bg-indigo-500 text-white'
                                    : 'text-gray-900'
                                    }`}
                                onClick={() => toggleOption(option)}
                            >
                                <span className="flex items-center">
                                    <span className="ml-3 block font-normal truncate">{option.label}</span>
                                </span>
                            </button>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default MultiSelectDropdown;